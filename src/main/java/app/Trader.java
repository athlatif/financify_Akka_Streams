package app;

public class Trader {

    private Integer currentBalance;
    private String traderId;
    private Integer traderStratgy;

    public Trader(Integer currentBalance, String traderId, Integer traderStratgy) {
        this.currentBalance = currentBalance;
        this.traderId = traderId;
        this.traderStratgy = traderStratgy;
    }

    public Integer getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Integer currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    public Integer getTraderStratgy() {
        return traderStratgy;
    }

    public void setTraderStratgy(Integer traderStratgy) {
        this.traderStratgy = traderStratgy;
    }

    public String applystratgy(String value){

        Integer casted_value = Integer.parseInt(value);

        switch (this.traderStratgy) {
            case 1:
                if(!(casted_value > this.currentBalance)){

                    if(casted_value > (this.currentBalance/2)){
                        value = "Sell";
                        this.currentBalance = this.currentBalance + casted_value;
                    }else{
                        value = "buy";
                        this.currentBalance = this.currentBalance - casted_value;
                    }

                } else {
                    value = "ignore";
                }
                break;
            case 2:
                if(!(casted_value > this.currentBalance)){

                    if(casted_value < (this.currentBalance/2)){
                        value = "Sell";
                        this.currentBalance = this.currentBalance + casted_value;
                    }else{
                        value = "buy";
                        this.currentBalance = this.currentBalance - casted_value;
                    }

                } else {
                    value = "ignore";
                }

        }




        return  value;

    }
}
