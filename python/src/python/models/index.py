"""
Model exports for Optikpi Data Pipeline SDK

This module provides convenient access to all data models used in the SDK.
"""

from .CustomerProfile import CustomerProfile
from .AccountEvent import AccountEvent
from .DepositEvent import DepositEvent
from .WithdrawEvent import WithdrawEvent
from .GamingActivityEvent import GamingActivityEvent

# Define public API for the models package
__all__ = [
    "CustomerProfile",
    "AccountEvent",
    "DepositEvent",
    "WithdrawEvent",
    "GamingActivityEvent"
]