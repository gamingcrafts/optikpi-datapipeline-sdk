#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

if [ -z "$1" ]; then
  echo -e "${RED}❌ Please provide class name${NC}"
  echo -e "${YELLOW}Example: ./run.sh TestAccountEndpoint${NC}"
  echo ""
  echo "Available classes:"
  echo "  - TestAccountEndpoint"
  echo "  - TestAllEndpoints"
  echo "  - TestCustomerEndpoint"
  echo "  - TestCustomerExtEndpoint"
  echo "  - TestDepositEndpoint"
  echo "  - TestGamingEndpoint"
  echo "  - TestReferFriendEndpoint"
  echo "  - TestWalletBalanceEndpoint"
  echo "  - TestWithdrawEndpoint"
  echo "  - TestBatchOperations"
  exit 1
fi

MAINCLASS="com.optikpi.examples.$1"

echo -e "${GREEN}🚀 Running $MAINCLASS...${NC}"
echo ""
mvn -q compile exec:java -Dexec.mainClass="$MAINCLASS"
