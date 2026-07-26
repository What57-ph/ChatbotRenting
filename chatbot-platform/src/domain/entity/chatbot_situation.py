import uuid
from datetime import datetime, timezone
from typing import TYPE_CHECKING
from sqlalchemy import String, Text, ForeignKey, DateTime, Index
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import Mapped, mapped_column, relationship

from db.base import Base


if TYPE_CHECKING:
    from entity.chatbot import Chatbot


class ChatbotSituation(Base):
    __tablename__ = "chatbot_situations"

    id: Mapped[uuid.UUID] = mapped_column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)

    chatbot_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("chatbots.id", ondelete="CASCADE"), nullable=False)

    name: Mapped[str] = mapped_column(String(150), nullable=False)

    instruction: Mapped[str] = mapped_column(Text, nullable=False)

    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        default=lambda: datetime.now(timezone.utc)
    )

    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        default=lambda: datetime.now(timezone.utc)
    )

    chatbot: Mapped["Chatbot"] = relationship("Chatbot", back_populates="situations")