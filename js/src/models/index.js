const CustomerProfile = require('./CustomerProfile');
const AccountEvent = require('./AccountEvent');
const DepositEvent = require('./DepositEvent');
const WithdrawEvent = require('./WithdrawEvent');
const GamingActivityEvent = require('./GamingActivityEvent');
const WalletBalanceEvent = require('./WalletBalanceEvent');
const ReferFriendEvent = require('./ReferFriendEvent');
const CustomerExtEvent = require('./CustomerExtEvent');
const SystemEvent = require('./SystemEvent');
const PurchaseFunnelEvent = require('./PurchaseFunnelEvent');

module.exports = {
  CustomerProfile,
  AccountEvent,
  DepositEvent,
  WithdrawEvent,
  GamingActivityEvent,
  WalletBalanceEvent,
  ReferFriendEvent,
  CustomerExtEvent,
  SystemEvent,
  PurchaseFunnelEvent,
  FUNNEL_STAGES: PurchaseFunnelEvent.FUNNEL_STAGES,
  FAILURE_REASONS: PurchaseFunnelEvent.FAILURE_REASONS
};
