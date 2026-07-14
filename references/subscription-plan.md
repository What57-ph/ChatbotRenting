## Business Logic & API Specification

---

## 📌 Tổng quan

Subscription Service quản lý toàn bộ vòng đời đăng ký gói dịch vụ của user: xem plan, đăng ký, gia hạn, hủy, nâng cấp, downgrade, theo dõi usage và giới hạn tài nguyên.

**Tech:** Java Spring Boot | **Schema:** `subscription` | **Port:** `8003`

---

## 🔐 Quy ước đặt tên API

| Prefix | Gọi bởi | Mô tả |
|---|---|---|
| `/client-api/v1/...` | Frontend (Dashboard, Widget) | Yêu cầu JWT user |
| `/service-api/v1/...` | Backend service nội bộ | Không cần JWT user, xác thực qua internal token hoặc network policy |
| Không prefix | Public | Không cần xác thực |

---

## 🔄 Subscription Status Flow

```
                    ┌─────────────────────────────────────────┐
                    │                                         │
          đăng ký   ▼        thanh toán thành công           │  gia hạn thành công
PENDING ──────────► ACTIVE ──────────────────────────────────┘
                    │
                    │ user hủy
                    ▼
            WAITING_TO_EXPIRED  ← status mới
                    │
                    │ đến currentPeriodEnd (cron job)
                    ▼
                  EXPIRED
                    │
                    │ thanh toán quá hạn
          PAST_DUE ◄┘
```

### Giải thích các status

| Status | Ý nghĩa | User còn dùng được không |
|---|---|---|
| `PENDING` | Vừa đăng ký, chờ thanh toán | ❌ |
| `ACTIVE` | Đang hoạt động bình thường | ✅ |
| `WAITING_TO_EXPIRED` | Đã hủy, đang chờ hết hạn kỳ hiện tại | ✅ (đến hết kỳ) |
| `EXPIRED` | Đã hết hạn hoàn toàn | ❌ |
| `PAST_DUE` | Quá hạn thanh toán gia hạn | ❌ |

---

## 🗄️ Entities & quan hệ

```
SubscriptionPlan
    └── PlanFeature (1-n)
    └── Subscription (1-n)
          └── Order (1-n)
                └── Invoice (1-1)
          └── UsageRecord (1-n)
          └── UsageSummary (1-n, unique per year+month)
```

---

## 📋 Subscription Plan APIs

---

### `GET /plans` — Danh sách plans

**Caller:** Client & Service  
**Auth:** Public

**Tác dụng:** Hiển thị tất cả gói dịch vụ đang active để user so sánh và chọn trước khi đăng ký. Cũng dùng cho service nội bộ khi cần lấy thông tin plan.

**Business logic:**
1. Query tất cả `SubscriptionPlan` có `active = true`
2. Kèm theo `PlanFeature` của từng plan
3. Sắp xếp theo `monthlyPrice` tăng dần

**Response `200`:**
```json
{
  "data": [
    {
      "id": "plan-uuid",
      "code": "free",
      "name": "Gói Miễn Phí",
      "description": "Dùng thử không giới hạn thời gian",
      "monthlyPrice": 0,
      "yearlyPrice": 0,
      "maxChatbots": 1,
      "maxStorageMb": 10,
      "maxMonthlyTokens": 50000,
      "durationMonths": null,
      "features": [
        { "featureKey": "zalo_integration", "featureValue": "false" },
        { "featureKey": "analytics", "featureValue": "false" }
      ]
    },
    {
      "id": "plan-uuid-2",
      "code": "starter",
      "name": "Gói Cơ Bản",
      "monthlyPrice": 299000,
      "yearlyPrice": 2990000,
      "maxChatbots": 3,
      "maxStorageMb": 100,
      "maxMonthlyTokens": 500000,
      "durationMonths": 1,
      "features": [
        { "featureKey": "zalo_integration", "featureValue": "true" },
        { "featureKey": "analytics", "featureValue": "true" }
      ]
    }
  ]
}
```

---

### `GET /plans/:planId` — Chi tiết một plan

**Caller:** Client & Service  
**Auth:** Public

**Tác dụng:** Lấy toàn bộ thông tin chi tiết của một plan. Dùng khi user click "Xem chi tiết" ở trang pricing, hoặc service nội bộ cần validate plan.

**Business logic:**
1. Query plan theo `planId`
2. Nếu `active = false` → vẫn trả về kèm `active: false` (admin hoặc service cần xem)
3. Kèm đầy đủ `PlanFeature`

**Response `200`:** Plan object đầy đủ kèm `active` field

**Errors:**
- `404` — plan không tồn tại

---

## 📋 Subscription APIs

---

### `POST /client-api/v1/subscriptions` — Đăng ký plan

**Caller:** Client (Frontend)  
**Auth:** JWT

**Tác dụng:** User chọn plan và chu kỳ thanh toán. Service tạo subscription, order và invoice. Subscription chỉ chuyển sang `ACTIVE` sau khi thanh toán thành công.

**Business logic:**
1. Lấy `userId` từ JWT
2. Kiểm tra user chưa có subscription với `status IN (PENDING, ACTIVE, WAITING_TO_EXPIRED)` → nếu có trả `409`
3. Query plan theo `planId`, kiểm tra `active = true`
4. Tính `amount` theo `billingCycle`:
    - `MONTHLY` → `monthlyPrice`
    - `YEARLY` → `yearlyPrice`
5. Tạo `Subscription` với `status = PENDING`:
    - `startDate = null`, `currentPeriodStart = null`, `currentPeriodEnd = null`
    - `autoRenew = true`
6. Tạo `Order`:
    - `orderType = NEW_SUBSCRIPTION`, `status = PENDING`
    - `orderNumber` = `ORD-{yyyyMMdd}-{random6}`
7. Tạo `Invoice`:
    - `status = UNPAID`
    - `invoiceNumber` = `INV-{yyyyMMdd}-{random6}`
    - `issuedAt = NOW()`, `dueDate = NOW() + 24h`
8. Return subscription + order + invoice để frontend redirect sang trang thanh toán

**Request:**
```json
{
  "planId": "plan-uuid",
  "billingCycle": "MONTHLY"
}
```

**Response `201`:**
```json
{
  "subscription": {
    "id": "sub-uuid",
    "status": "PENDING",
    "plan": { "id": "plan-uuid", "name": "Gói Cơ Bản", "code": "starter" },
    "autoRenew": true
  },
  "order": {
    "id": "order-uuid",
    "orderNumber": "ORD-20250612-A3F9K2",
    "amount": 299000,
    "currency": "VND",
    "billingCycle": "MONTHLY",
    "status": "PENDING",
    "orderType": "NEW_SUBSCRIPTION"
  },
  "invoice": {
    "id": "invoice-uuid",
    "invoiceNumber": "INV-20250612-B7X1P4",
    "amount": 299000,
    "currency": "VND",
    "status": "UNPAID",
    "issuedAt": "2025-06-12T10:00:00Z",
    "dueDate": "2025-06-13T10:00:00Z"
  }
}
```

**Errors:**
- `400` — billingCycle không hợp lệ
- `404` — plan không tồn tại hoặc inactive
- `409` — user đã có subscription PENDING, ACTIVE hoặc WAITING_TO_EXPIRED

---

### `GET /client-api/v1/subscriptions/me` — Subscription hiện tại

**Caller:** Client (Frontend)  
**Auth:** JWT

**Tác dụng:** Lấy thông tin subscription hiện tại của user kèm usage tháng hiện tại. Hiển thị trên trang billing dashboard.

**Business logic:**
1. Lấy `userId` từ JWT
2. Query subscription có `status IN (ACTIVE, WAITING_TO_EXPIRED, PENDING)` của user
    - `WAITING_TO_EXPIRED` vẫn hiển thị vì user còn đang sử dụng đến hết kỳ
3. Kèm plan info và `UsageSummary` kỳ hiện tại
4. Tính phần trăm usage từng loại tài nguyên

**Response `200`:**
```json
{
  "id": "sub-uuid",
  "status": "WAITING_TO_EXPIRED",
  "autoRenew": false,
  "startDate": "2025-06-01T00:00:00Z",
  "currentPeriodStart": "2025-06-01T00:00:00Z",
  "currentPeriodEnd": "2025-07-01T00:00:00Z",
  "cancelledAt": "2025-06-15T09:00:00Z",
  "plan": {
    "id": "plan-uuid",
    "code": "starter",
    "name": "Gói Cơ Bản",
    "monthlyPrice": 299000
  },
  "usage": {
    "tokensUsed": 123000,
    "tokensLimit": 500000,
    "tokensPercent": 24.6,
    "storageUsedMb": 45,
    "storageLimitMb": 100,
    "storagePercent": 45.0,
    "chatbotCount": 2,
    "chatbotLimit": 3,
    "filesCount": 8
  }
}
```

**Errors:**
- `404` — user không có subscription nào

---

### `GET /service-api/v1/subscriptions/user/:userId` — Lấy subscription theo userId

**Caller:** Service (Chat Service, Core Service, Ingestion Service)  
**Auth:** Internal token

**Tác dụng:** Service nội bộ lấy thông tin subscription của một user để check quyền truy cập, plan limits trước khi xử lý request.

**Business logic:**
1. Query subscription `ACTIVE` hoặc `WAITING_TO_EXPIRED` theo `userId`
2. Kèm plan info với đầy đủ limits

**Response `200`:**
```json
{
  "id": "sub-uuid",
  "userId": 123,
  "status": "ACTIVE",
  "plan": {
    "code": "starter",
    "maxChatbots": 3,
    "maxStorageMb": 100,
    "maxMonthlyTokens": 500000,
    "features": {
      "zalo_integration": "true",
      "api_access": "false"
    }
  },
  "currentPeriodEnd": "2025-07-01T00:00:00Z"
}
```

**Errors:**
- `404` — user không có subscription active

---

### `DELETE /client-api/v1/subscriptions/me` — Hủy subscription

**Caller:** Client (Frontend)  
**Auth:** JWT

**Tác dụng:** User hủy đăng ký. Subscription không dừng ngay mà chuyển sang `WAITING_TO_EXPIRED` — user vẫn dùng được đến hết `currentPeriodEnd`. Cron job sẽ chuyển sang `EXPIRED` khi đến ngày.

**Business logic:**
1. Lấy `userId` từ JWT
2. Query subscription của user
3. Validate trạng thái hiện tại:
    - `PENDING` → `400` — chưa active, không cần hủy, dùng endpoint xóa đơn hàng
    - `WAITING_TO_EXPIRED` → `409` — đã hủy rồi
    - `EXPIRED` → `409` — đã hết hạn rồi
    - `PAST_DUE` → `409` — đang quá hạn, không thể hủy
4. Cập nhật subscription:
    - `status = WAITING_TO_EXPIRED`
    - `cancelledAt = NOW()`
    - `autoRenew = false`
    - `currentPeriodEnd` giữ nguyên — user dùng đến hết kỳ đã thanh toán
5. Không tạo order hay invoice mới

**Response `200`:**
```json
{
  "id": "sub-uuid",
  "status": "WAITING_TO_EXPIRED",
  "cancelledAt": "2025-06-15T09:00:00Z",
  "currentPeriodEnd": "2025-07-01T00:00:00Z",
  "message": "Subscription đã hủy. Bạn vẫn có thể sử dụng dịch vụ đến 01/07/2025."
}
```

**Errors:**
- `400` — subscription đang PENDING, không thể hủy
- `404` — không có subscription
- `409` — subscription đã WAITING_TO_EXPIRED, EXPIRED hoặc PAST_DUE

---

### `POST /client-api/v1/subscriptions/upgrade` — Nâng cấp plan

**Caller:** Client (Frontend)  
**Auth:** JWT

**Tác dụng:** User nâng cấp lên plan cao hơn. Hiệu lực ngay. Tạo order và invoice cho phần chênh lệch còn lại trong kỳ (prorated).

**Business logic:**
1. Lấy `userId` từ JWT
2. Query subscription, validate:
    - Không phải `ACTIVE` → `400` — chỉ upgrade khi đang active
    - `WAITING_TO_EXPIRED` → `400` — đã hủy rồi, không thể upgrade
3. Lấy plan mới, kiểm tra `active = true`
4. So sánh plan:
    - Cùng plan → `400` với `SAME_PLAN`
    - Plan mới giá thấp hơn → `400` với `USE_DOWNGRADE_ENDPOINT`
5. Tính `proratedAmount`:
    - `remainingDays / totalDaysInPeriod × (newMonthlyPrice - currentMonthlyPrice)`
6. Cập nhật subscription: `previousPlan = plan cũ`, `plan = plan mới`
7. Tạo `Order` với `orderType = UPGRADE`, `amount = proratedAmount`
8. Tạo `Invoice` tương ứng `status = UNPAID`
9. Giới hạn tài nguyên mới áp dụng ngay, kể cả trước khi thanh toán prorated

**Request:**
```json
{
  "planId": "plan-uuid-growth",
  "billingCycle": "MONTHLY"
}
```

**Response `200`:**
```json
{
  "subscription": {
    "id": "sub-uuid",
    "status": "ACTIVE",
    "plan": { "code": "growth", "name": "Gói Tăng Trưởng" },
    "previousPlan": { "code": "starter", "name": "Gói Cơ Bản" }
  },
  "order": {
    "orderNumber": "ORD-20250615-C9D2E1",
    "amount": 133548,
    "orderType": "UPGRADE",
    "status": "PENDING"
  },
  "invoice": {
    "invoiceNumber": "INV-20250615-F4G5H6",
    "amount": 133548,
    "status": "UNPAID"
  }
}
```

**Errors:**
- `400` — `SAME_PLAN` | `USE_DOWNGRADE_ENDPOINT` | subscription không ACTIVE
- `404` — subscription hoặc plan không tồn tại

---

### `POST /client-api/v1/subscriptions/downgrade` — Hạ cấp plan

**Caller:** Client (Frontend)  
**Auth:** JWT

**Tác dụng:** User hạ cấp xuống plan thấp hơn. Khác với upgrade, downgrade **không** có hiệu lực ngay — áp dụng từ kỳ thanh toán tiếp theo để tránh hoàn tiền phức tạp.

**Business logic:**
1. Lấy `userId` từ JWT
2. Query subscription, validate:
    - Không phải `ACTIVE` → `400`
    - `WAITING_TO_EXPIRED` → `400` — đã hủy, không thể downgrade
3. Lấy plan mới, kiểm tra `active = true`
4. So sánh plan:
    - Cùng plan → `400` với `SAME_PLAN`
    - Plan mới giá cao hơn → `400` với `USE_UPGRADE_ENDPOINT`
5. Kiểm tra usage hiện tại có vượt giới hạn plan mới không:
    - `chatbotCount > newPlan.maxChatbots` → `400` với `CHATBOT_LIMIT_EXCEEDED` kèm danh sách cần xóa bớt
    - `storageUsedMb > newPlan.maxStorageMb` → `400` với `STORAGE_LIMIT_EXCEEDED`
6. Lưu `scheduledPlanId` vào subscription — plan mới sẽ áp dụng kỳ sau
7. Cron job renewal sẽ đọc `scheduledPlanId` và áp dụng khi gia hạn
8. Không tạo order hay invoice mới

**Request:**
```json
{
  "planId": "plan-uuid-free"
}
```

**Response `200`:**
```json
{
  "id": "sub-uuid",
  "status": "ACTIVE",
  "currentPlan": { "code": "starter", "name": "Gói Cơ Bản" },
  "scheduledPlan": { "code": "free", "name": "Gói Miễn Phí" },
  "effectiveDate": "2025-07-01T00:00:00Z",
  "message": "Gói sẽ được hạ cấp xuống Gói Miễn Phí từ 01/07/2025."
}
```

**Errors:**
- `400` — `SAME_PLAN` | `USE_UPGRADE_ENDPOINT` | `CHATBOT_LIMIT_EXCEEDED` | `STORAGE_LIMIT_EXCEEDED` | subscription không ACTIVE
- `404` — subscription hoặc plan không tồn tại

---

### `PATCH /client-api/v1/subscriptions/me/auto-renew` — Bật/tắt tự động gia hạn

**Caller:** Client (Frontend)  
**Auth:** JWT

**Tác dụng:** User kiểm soát tự động gia hạn. Tắt không hủy ngay — chỉ ảnh hưởng kỳ tiếp theo.

**Business logic:**
1. Query subscription `ACTIVE` hoặc `WAITING_TO_EXPIRED` của user
2. Cập nhật `autoRenew`
3. Nếu subscription đang `WAITING_TO_EXPIRED` và bật `autoRenew = true`:
    - Chuyển status về `ACTIVE`
    - Xóa `cancelledAt`

**Request:**
```json
{
  "autoRenew": false
}
```

**Response `200`:**
```json
{
  "id": "sub-uuid",
  "status": "ACTIVE",
  "autoRenew": false,
  "message": "Subscription sẽ hết hạn vào 01/07/2025 và không tự động gia hạn."
}
```

**Errors:**
- `404` — không có subscription

---

## 📋 Invoice & Payment Status APIs

---

### `PATCH /service-api/v1/invoices/:invoiceId/status` — Cập nhật trạng thái invoice và subscription

**Caller:** Service (Payment Gateway callback handler)  
**Auth:** Internal token

**Tác dụng:** Cập nhật trạng thái invoice sau khi payment gateway phản hồi. Xử lý cả trường hợp thanh toán thành công lẫn thất bại. Từ trạng thái invoice sẽ kéo theo cập nhật subscription tương ứng.

**Validate transition hợp lệ:**

| Từ | Sang | Điều kiện |
|---|---|---|
| `UNPAID` | `PAID` | Thanh toán thành công |
| `UNPAID` | `FAILED` | Thanh toán thất bại |
| `UNPAID` | `CANCELLED` | Hủy đơn trước khi thanh toán |
| `FAILED` | `PAID` | Retry thanh toán thành công |
| `PAID` | bất kỳ | ❌ Không cho phép |
| `CANCELLED` | bất kỳ | ❌ Không cho phép |

**Business logic:**

**Khi `status = PAID`:**
1. Validate invoice chưa PAID hoặc CANCELLED → nếu vi phạm trả `409`
2. Cập nhật `Invoice`: `status = PAID`, `paidAt = NOW()`, `paymentMethod`, `paymentReference`
3. Cập nhật `Order`: `status = PAID`
4. Cập nhật `Subscription` theo `orderType` của order:
    - `NEW_SUBSCRIPTION` hoặc `RENEWAL`:
        - `status = ACTIVE`
        - `startDate = NOW()` (chỉ set nếu null)
        - `currentPeriodStart = NOW()`
        - `currentPeriodEnd = NOW() + plan.durationMonths`
        - Nếu có `scheduledPlanId` (downgrade đã đặt lịch) → áp dụng plan mới, xóa `scheduledPlanId`
    - `UPGRADE`:
        - Giữ nguyên period, plan đã cập nhật ở bước upgrade
    - `DOWNGRADE`:
        - Không áp dụng ở đây — downgrade áp dụng từ kỳ tiếp, xử lý qua renewal
5. Tạo `UsageSummary` cho kỳ hiện tại nếu chưa có (counters = 0)

**Khi `status = FAILED`:**
1. Validate invoice chưa PAID hoặc CANCELLED → nếu vi phạm trả `409`
2. Cập nhật `Invoice`: `status = FAILED`
3. Cập nhật `Order`: `status = FAILED`
4. Cập nhật `Subscription`:
    - Nếu `orderType = NEW_SUBSCRIPTION` → `status = PENDING` (giữ nguyên, user có thể thử lại)
    - Nếu `orderType = RENEWAL` → `status = PAST_DUE` (đã hết kỳ nhưng chưa thanh toán được)
    - Nếu `orderType = UPGRADE` → hoàn lại `plan = previousPlan` (rollback)
5. Gửi notification cho user để thử lại thanh toán

**Khi `status = CANCELLED`:**
1. Validate invoice đang UNPAID hoặc FAILED
2. Cập nhật `Invoice`: `status = CANCELLED`
3. Cập nhật `Order`: `status = CANCELLED`
4. Nếu `orderType = NEW_SUBSCRIPTION` → `status = EXPIRED` (hủy đăng ký luôn)

**Request:**
```json
{
  "status": "PAID",
  "paymentMethod": "momo",
  "paymentReference": "MOMO-TXN-123456",
  "notes": "Thanh toán qua MoMo lúc 10:28"
}
```

| Field | Bắt buộc | Mô tả |
|---|---|---|
| status | ✅ | `PAID` \| `FAILED` \| `CANCELLED` |
| paymentMethod | Khi PAID | `momo` \| `vnpay` \| `bank_transfer` |
| paymentReference | Khi PAID | Mã giao dịch từ cổng thanh toán |
| notes | ❌ | Ghi chú thêm |

**Response `200`:**
```json
{
  "invoiceId": "invoice-uuid",
  "invoiceStatus": "PAID",
  "orderStatus": "PAID",
  "subscriptionStatus": "ACTIVE",
  "currentPeriodStart": "2025-06-12T10:28:00Z",
  "currentPeriodEnd": "2025-07-12T10:28:00Z"
}
```

**Response khi FAILED:**
```json
{
  "invoiceId": "invoice-uuid",
  "invoiceStatus": "FAILED",
  "orderStatus": "FAILED",
  "subscriptionStatus": "PAST_DUE",
  "message": "Thanh toán thất bại. Subscription chuyển sang PAST_DUE."
}
```

**Errors:**
- `400` — `status` không hợp lệ hoặc thiếu `paymentMethod` khi PAID
- `404` — invoice không tồn tại
- `409` — transition không được phép (ví dụ từ PAID sang FAILED)

---

### `GET /client-api/v1/invoices` — Danh sách invoices

**Caller:** Client (Frontend)  
**Auth:** JWT

**Tác dụng:** Hiển thị lịch sử hóa đơn để user kiểm tra trạng thái thanh toán.

**Business logic:**
1. Lấy `userId` từ JWT
2. Query invoices qua join `orders` → `invoices` theo `userId`
3. Sắp xếp `issuedAt` giảm dần, hỗ trợ phân trang

**Response `200`:**
```json
{
  "data": [
    {
      "id": "invoice-uuid",
      "invoiceNumber": "INV-20250612-B7X1P4",
      "amount": 299000,
      "currency": "VND",
      "status": "PAID",
      "paymentMethod": "momo",
      "issuedAt": "2025-06-12T10:00:00Z",
      "paidAt": "2025-06-12T10:28:00Z",
      "dueDate": "2025-06-13T10:00:00Z",
      "order": {
        "orderNumber": "ORD-20250612-A3F9K2",
        "orderType": "NEW_SUBSCRIPTION"
      }
    }
  ],
  "pagination": { "page": 1, "limit": 20, "total": 3 }
}
```

---

### `GET /client-api/v1/invoices/:invoiceId` — Chi tiết invoice

**Caller:** Client (Frontend)  
**Auth:** JWT

**Tác dụng:** Xem đầy đủ một hóa đơn kèm order liên kết.

**Business logic:**
1. Query invoice theo `invoiceId`
2. Verify thuộc user qua `order.userId`
3. Kèm order detail đầy đủ

**Response `200`:** Invoice object đầy đủ kèm order detail

**Errors:**
- `404` — invoice không tồn tại hoặc không thuộc user

---

## 📋 Order APIs

---

### `GET /client-api/v1/orders` — Danh sách orders

**Caller:** Client (Frontend)  
**Auth:** JWT

**Tác dụng:** Lịch sử tất cả đơn hàng của user gồm mua mới, gia hạn, nâng cấp, hạ cấp.

**Business logic:**
1. Lấy `userId` từ JWT
2. Query tất cả orders của user, kèm plan info và invoice status
3. Sắp xếp `createdAt` giảm dần

**Query params:** `page`, `limit`, `status`

**Response `200`:**
```json
{
  "data": [
    {
      "id": "order-uuid",
      "orderNumber": "ORD-20250612-A3F9K2",
      "amount": 299000,
      "currency": "VND",
      "billingCycle": "MONTHLY",
      "status": "PAID",
      "orderType": "NEW_SUBSCRIPTION",
      "plan": { "code": "starter", "name": "Gói Cơ Bản" },
      "invoice": {
        "invoiceNumber": "INV-20250612-B7X1P4",
        "status": "PAID",
        "paidAt": "2025-06-12T10:30:00Z"
      },
      "createdAt": "2025-06-12T10:00:00Z"
    }
  ],
  "pagination": { "page": 1, "limit": 20, "total": 5 }
}
```

---

### `GET /client-api/v1/orders/:orderId` — Chi tiết order

**Caller:** Client (Frontend)  
**Auth:** JWT

**Tác dụng:** Xem đầy đủ một đơn hàng kèm invoice.

**Business logic:**
1. Query order theo `orderId`
2. Verify thuộc `userId` trong JWT
3. Kèm đầy đủ invoice và plan info

**Response `200`:** Order object đầy đủ

**Errors:**
- `404` — order không tồn tại hoặc không thuộc user

---

## 📋 Usage APIs

---

### `POST /service-api/v1/usage/record` — Ghi nhận usage event

**Caller:** Service (Chat Service, Ingestion Service, Core Service)  
**Auth:** Internal token

**Tác dụng:** Ghi lại mỗi sự kiện tiêu thụ tài nguyên vào `UsageRecord` và cập nhật tổng hợp vào `UsageSummary`.

**Mapping UsageType → UsageSummary:**

| usageType | Tác động |
|---|---|
| `TOKEN_USED` | `tokensUsed += quantity` |
| `FILE_UPLOADED` | `storageUsedMb += quantity`, `filesCount += 1` |
| `FILE_DELETED` | `storageUsedMb -= quantity`, `filesCount -= 1` |
| `CHATBOT_CREATED` | `chatbotCount += 1` |
| `CHATBOT_DELETED` | `chatbotCount -= 1` |
| `API_CALL` | `apiCalls += 1` |

**Business logic:**
1. Query subscription `ACTIVE` hoặc `WAITING_TO_EXPIRED` theo `userId`
2. Tạo `UsageRecord`
3. Upsert `UsageSummary` tháng hiện tại, cập nhật counter
4. Không block nếu vượt limit — chỉ ghi nhận

**Request:**
```json
{
  "userId": 123,
  "botId": "bot-uuid",
  "usageType": "TOKEN_USED",
  "quantity": 450
}
```

**Response `201`:**
```json
{
  "recorded": true,
  "usageType": "TOKEN_USED",
  "quantity": 450,
  "summary": {
    "tokensUsed": 123450,
    "tokensLimit": 500000
  }
}
```

---

### `POST /service-api/v1/usage/check` — Kiểm tra quota

**Caller:** Service (Chat Service, Ingestion Service)  
**Auth:** Internal token

**Tác dụng:** Service gọi trước khi xử lý request để xác định user còn quota không. Đây là điểm kiểm soát duy nhất — nếu hết quota sẽ block xử lý.

**Business logic:**
1. Query subscription của `userId`
2. Nếu không có subscription active → `allowed: false`, reason `NO_ACTIVE_SUBSCRIPTION`
3. Nếu `WAITING_TO_EXPIRED` nhưng còn hạn → vẫn cho phép dùng
4. Lấy `UsageSummary` kỳ hiện tại
5. So sánh với plan limits theo `usageType`

**Request:**
```json
{
  "userId": 123,
  "usageType": "TOKEN_USED"
}
```

**Response `200` — Còn quota:**
```json
{
  "allowed": true,
  "current": 123000,
  "limit": 500000,
  "remaining": 377000
}
```

**Response `200` — Hết quota:**
```json
{
  "allowed": false,
  "reason": "TOKEN_LIMIT_EXCEEDED",
  "current": 500000,
  "limit": 500000,
  "remaining": 0,
  "upgradeRequired": true,
  "message": "Bạn đã dùng hết token trong tháng này. Vui lòng nâng cấp gói."
}
```

**Response `200` — Không có subscription:**
```json
{
  "allowed": false,
  "reason": "NO_ACTIVE_SUBSCRIPTION",
  "upgradeRequired": false
}
```

---

### `GET /client-api/v1/usage/summary` — Usage tháng hiện tại

**Caller:** Client (Frontend)  
**Auth:** JWT

**Tác dụng:** Hiển thị mức sử dụng tài nguyên trong kỳ hiện tại so với giới hạn plan. Dùng trên dashboard và trang billing.

**Business logic:**
1. Query subscription `ACTIVE` hoặc `WAITING_TO_EXPIRED` của user
2. Lấy `UsageSummary` theo `currentPeriodStart` (không phải tháng calendar)
3. Tính phần trăm từng loại

**Response `200`:**
```json
{
  "period": {
    "start": "2025-06-12T10:28:00Z",
    "end": "2025-07-12T10:28:00Z"
  },
  "plan": { "code": "starter", "name": "Gói Cơ Bản" },
  "usage": {
    "tokens": { "used": 123000, "limit": 500000, "percent": 24.6 },
    "storage": { "usedMb": 45, "limitMb": 100, "percent": 45.0 },
    "chatbots": { "count": 2, "limit": 3 },
    "files": { "count": 8 },
    "apiCalls": 340
  }
}
```

**Errors:**
- `404` — user không có subscription

---

### `GET /client-api/v1/usage/history` — Lịch sử usage

**Caller:** Client (Frontend)  
**Auth:** JWT

**Tác dụng:** Xem usage các kỳ thanh toán trước để theo dõi xu hướng.

**Business logic:**
1. Query tất cả `UsageSummary` thuộc subscription của user
2. Loại trừ kỳ hiện tại
3. Sắp xếp `year` và `month` giảm dần

**Response `200`:**
```json
{
  "data": [
    {
      "year": 2025, "month": 5,
      "tokensUsed": 387000, "storageUsedMb": 40,
      "chatbotCount": 2, "filesCount": 6, "apiCalls": 890
    }
  ]
}
```

---

## 📋 Renewal & Expiry (Scheduled Jobs)

---

### Job 1: Auto-renew — Chạy lúc 01:00 mỗi ngày

**Tác dụng:** Tự động tạo order gia hạn cho subscription sắp hết kỳ.

**Business logic:**
1. Query subscription có:
    - `status = ACTIVE`
    - `autoRenew = true`
    - `currentPeriodEnd <= NOW() + 1 ngày`
2. Validate từng subscription trước khi xử lý:
    - Plan vẫn còn `active = true` → nếu không, gửi email thông báo plan bị ngừng
    - Chưa có renewal order PENDING cho kỳ này → tránh tạo trùng
3. Với mỗi subscription hợp lệ:
    - Kiểm tra `scheduledPlanId` — nếu có, dùng plan đó thay vì plan hiện tại (downgrade đã đặt lịch)
    - Tạo `Order`: `orderType = RENEWAL`, `status = PENDING`
    - Tạo `Invoice`: `status = UNPAID`, `dueDate = NOW() + 24h`
    - Gửi notification cho user kèm link thanh toán
4. Log kết quả: số subscription xử lý thành công, số bị lỗi, lý do lỗi

**Lỗi và xử lý:**

| Tình huống | Xử lý |
|---|---|
| Plan bị deactivate | Skip subscription, gửi email cảnh báo, log lỗi `PLAN_INACTIVE` |
| Đã có renewal order PENDING | Skip, log `RENEWAL_ALREADY_PENDING` |
| DB error khi tạo order | Log lỗi, tiếp tục với subscription tiếp theo, không dừng toàn bộ job |
| Invoice quá hạn 3 ngày chưa thanh toán | Chuyển subscription sang `PAST_DUE` |

---

### Job 2: Expire subscription — Chạy lúc 02:00 mỗi ngày

**Tác dụng:** Chuyển subscription sang `EXPIRED` khi đã hết kỳ và không còn active.

**Business logic:**
1. Query subscription có:
    - `status = WAITING_TO_EXPIRED`
    - `currentPeriodEnd < NOW()`
2. Validate từng subscription:
    - `currentPeriodEnd` không null → nếu null thì skip và log cảnh báo
    - `status` vẫn là `WAITING_TO_EXPIRED` tại thời điểm xử lý → tránh race condition
3. Với mỗi subscription hợp lệ:
    - Cập nhật `status = EXPIRED`
    - Gửi notification cho user về việc hết hạn, gợi ý đăng ký lại
    - Log: `subscriptionId`, `userId`, `expiredAt`
4. Query thêm subscription có:
    - `status = PAST_DUE`
    - `currentPeriodEnd < NOW() - 7 ngày` (quá hạn quá 7 ngày)
    - Chuyển sang `EXPIRED`

**Lỗi và xử lý:**

| Tình huống | Xử lý |
|---|---|
| `currentPeriodEnd` null | Skip, log cảnh báo `MISSING_PERIOD_END` |
| Status đã thay đổi (race condition) | Skip, log `STATUS_CHANGED_BEFORE_EXPIRY` |
| DB error khi update | Log lỗi, retry sau 1 giờ tối đa 3 lần |
| Notification gửi thất bại | Log lỗi nhưng không rollback update status |

---

## ❌ Error Response Format

```json
{
  "error": "ERROR_CODE",
  "message": "Mô tả lỗi rõ ràng cho người dùng",
  "timestamp": "2025-06-15T10:00:00Z"
}
```

| HTTP | Error Code | Tình huống |
|---|---|---|
| 400 | `INVALID_BILLING_CYCLE` | billingCycle không phải MONTHLY/YEARLY |
| 400 | `SAME_PLAN` | Upgrade/downgrade đúng plan hiện tại |
| 400 | `USE_DOWNGRADE_ENDPOINT` | Upgrade nhưng plan có giá thấp hơn |
| 400 | `USE_UPGRADE_ENDPOINT` | Downgrade nhưng plan có giá cao hơn |
| 400 | `CHATBOT_LIMIT_EXCEEDED` | Downgrade nhưng đang dùng vượt giới hạn bot mới |
| 400 | `STORAGE_LIMIT_EXCEEDED` | Downgrade nhưng storage vượt giới hạn plan mới |
| 400 | `SUBSCRIPTION_PENDING` | Subscription đang PENDING, không thể hủy |
| 400 | `SUBSCRIPTION_NOT_ACTIVE` | Hành động yêu cầu subscription ACTIVE |
| 404 | `PLAN_NOT_FOUND` | Plan không tồn tại hoặc inactive |
| 404 | `SUBSCRIPTION_NOT_FOUND` | Không có subscription |
| 404 | `ORDER_NOT_FOUND` | Order không tồn tại hoặc không thuộc user |
| 404 | `INVOICE_NOT_FOUND` | Invoice không tồn tại |
| 409 | `SUBSCRIPTION_ALREADY_EXISTS` | Đã có subscription PENDING/ACTIVE/WAITING_TO_EXPIRED |
| 409 | `ALREADY_WAITING_TO_EXPIRED` | Subscription đã hủy rồi |
| 409 | `INVALID_STATUS_TRANSITION` | Transition invoice không được phép |

---

## 📡 API Summary

| Method | Endpoint | Auth | Caller | Tác dụng |
|---|---|---|---|---|
| GET | `/plans` | Public | Client & Service | Danh sách plans |
| GET | `/plans/:planId` | Public | Client & Service | Chi tiết plan |
| POST | `/client-api/v1/subscriptions` | JWT | Client | Đăng ký plan |
| GET | `/client-api/v1/subscriptions/me` | JWT | Client | Subscription hiện tại + usage |
| GET | `/service-api/v1/subscriptions/user/:userId` | Internal | Service | Subscription theo userId |
| DELETE | `/client-api/v1/subscriptions/me` | JWT | Client | Hủy → WAITING_TO_EXPIRED |
| POST | `/client-api/v1/subscriptions/upgrade` | JWT | Client | Nâng cấp plan, hiệu lực ngay |
| POST | `/client-api/v1/subscriptions/downgrade` | JWT | Client | Hạ cấp plan, hiệu lực kỳ sau |
| PATCH | `/client-api/v1/subscriptions/me/auto-renew` | JWT | Client | Bật/tắt tự động gia hạn |
| PATCH | `/service-api/v1/invoices/:invoiceId/status` | Internal | Service | Cập nhật status invoice + subscription |
| GET | `/client-api/v1/invoices` | JWT | Client | Lịch sử invoices |
| GET | `/client-api/v1/invoices/:invoiceId` | JWT | Client | Chi tiết invoice |
| GET | `/client-api/v1/orders` | JWT | Client | Lịch sử orders |
| GET | `/client-api/v1/orders/:orderId` | JWT | Client | Chi tiết order |
| POST | `/service-api/v1/usage/record` | Internal | Service | Ghi nhận usage event |
| POST | `/service-api/v1/usage/check` | Internal | Service | Kiểm tra quota |
| GET | `/client-api/v1/usage/summary` | JWT | Client | Usage kỳ hiện tại |
| GET | `/client-api/v1/usage/history` | JWT | Client | Lịch sử usage |

---

*Subscription Service v2.0 — Antigravity Platform*