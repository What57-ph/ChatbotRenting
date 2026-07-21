import uuid
from datetime import datetime, timezone
from enum import Enum as PyEnum
from typing import TYPE_CHECKING

from sqlalchemy import DateTime, String, ForeignKey, UUID
from sqlalchemy import Enum as SqlEnum
from sqlalchemy.orm import Mapped, mapped_column, relationship

from src.db.base import Base

if TYPE_CHECKING:
    from src.domain.entity.chatbot import Chatbot

class KnowledgeSourceType(str, PyEnum):
    FILE = "FILE"
    URL = "URL"
    TEXT = "TEXT"

class ProcessingStatus(str, PyEnum):
    CREATED = "CREATED"
    PROCESSING = "PROCESSING"
    COMPLETED = "COMPLETED"
    FAILED = "FAILED"
    DELETED = "DELETED"

class KnowledgeSource(Base):
    __tablename__ = "knowledge_sources"

    id: Mapped[uuid.UUID] = mapped_column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)

    chatbot_id: Mapped[uuid.UUID] = mapped_column(
        ForeignKey("chatbots.id", ondelete="CASCADE"), 
        nullable=False,
        index=True
    )

    name: Mapped[str] = mapped_column(String(255), nullable=False)

    source_type: Mapped[KnowledgeSourceType] = mapped_column(
        SqlEnum(KnowledgeSourceType, native_enum=False, length=20),
        nullable=False
    )
    status: Mapped[ProcessingStatus] = mapped_column(
        SqlEnum(ProcessingStatus, native_enum=False, length=20),
        nullable=False
    )

    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        default=lambda: datetime.now(timezone.utc)
    )

    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        default=lambda: datetime.now(timezone.utc),
        onupdate=lambda: datetime.now(timezone.utc)
    )
    deleted_at: Mapped[datetime | None] = mapped_column(
        DateTime(timezone=True), 
        nullable=True, 
        default=None
    )

    chatbot: Mapped["Chatbot"] = relationship("Chatbot", back_populates="knowledge_sources")
