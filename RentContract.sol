contract RentContract{
	mapping (address => uint256) public amountPaid;
	mapping (address => uint) public enteringTime;
	mapping (address => uint) public stayTimeDays;
	address renter;
	uint dailyCost;

	constructor (uint cost) public{
		renter = msg.sender;
		dailyCost = cost;
	}

	//called by the guest
	function pay(uint amountOfDays) payable public{
		require(msg.value == dailyCost*amountOfDays);
		//if true, money is already transferred to the contract

		amountPaid[msg.sender] += dailyCost*amountOfDays;
		enteringTime[msg.sender] = now;
		stayTimeDays[msg.sender] = amountOfDays;
	}

	//called by the renter
	function withdraw(address guest) public{
		require(msg.sender==renter);
		if((now - enteringTime[guest])/60/60/24 >= stayTimeDays[guest]){
			msg.sender.transfer(amountPaid[guest]);
			amountPaid[guest] = 0;
			enteringTime[guest] = 0;
			stayTimeDays[guest] = 0;
		}
	}
	
	//called by anyone
	function checkPayment(address guest) public returns(bool){
		return amountPaid[guest] > 0;
	}
}
