import uuid
from datetime import datetime, timezone
from typing import TYPE_CHECKING

from sqlalchemy import DateTime, String, UUID, Text
from enum import Enum as PyEnum
from sqlalchemy import Enum as SqlEnum
from sqlalchemy.orm import Mapped, mapped_column, relationship

from db.base import Base

if TYPE_CHECKING:
    from entity.chatbot import Chatbot
    from entity.chatbot_situation import ChatbotSituation
    from entity.knowledge_source import KnowledgeSource

class ChatbotStatus(str, PyEnum):
    ACTIVE = "ACTIVE"
    INACTIVE = "INACTIVE"
    DELETED = "DELETED"

class ChatbotLanguage(str, PyEnum):
    VIETNAMESE = "VIETNAMESE"
    ENGLISH = "ENGLISH"

class Chatbot(Base):
    __tablename__ = "chatbots"

    id: Mapped[uuid.UUID] = mapped_column(
        primary_key=True,
        default=uuid.uuid4
    )

    user_id: Mapped[uuid.UUID] = mapped_column(
        UUID(as_uuid=True),
        name="user_id",
        nullable=False
    )

    name: Mapped[str] = mapped_column(
        String(100),
        nullable=False
    )

    description: Mapped[str] = mapped_column(
        Text,
        nullable=True
    )

    avatar_url: Mapped[str] = mapped_column(
        Text,
        name="avatar_url"
    )

    system_prompt: Mapped[str] = mapped_column(
        Text,
        name="system_prompt"
    )

    status: Mapped[ChatbotStatus] = mapped_column(
        SqlEnum(ChatbotStatus, native_enum=False, length=20),
        nullable=False
    )

    language: Mapped[ChatbotLanguage] = mapped_column(
        SqlEnum(ChatbotLanguage, native_enum=False, length=10),
        nullable=False
    )

    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        default=lambda: datetime.now(timezone.utc)
    )

    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        default=lambda: datetime.now(timezone.utc)
    )

    deleted_at: Mapped[datetime | None] = mapped_column(
        DateTime(timezone=True),
        nullable=True,
        default=None
    )

    situations: Mapped[list["ChatbotSituation"]] = relationship(
        "ChatbotSituation",
        back_populates="chatbot",
        lazy="select",
    )

    knowledge_sources: Mapped[list["KnowledgeSource"]] = relationship(
        "KnowledgeSource",
        back_populates="chatbot",
        lazy="select"
    )