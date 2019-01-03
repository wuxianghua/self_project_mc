package net.imoran.auto.music.network.bean;

public class AcrRootBean extends BaseBean {
    private AcrMetaDataBean metadata;
    private double cost_time;
    private int record_time;
    private int result_type;

    public AcrMetaDataBean getMetadata() {
        return metadata;
    }

    public void setMetadata(AcrMetaDataBean metadata) {
        this.metadata = metadata;
    }

    public double getCost_time() {
        return cost_time;
    }

    public void setCost_time(double cost_time) {
        this.cost_time = cost_time;
    }

    public int getRecord_time() {
        return record_time;
    }

    public void setRecord_time(int record_time) {
        this.record_time = record_time;
    }

    public int getResult_type() {
        return result_type;
    }

    public void setResult_type(int result_type) {
        this.result_type = result_type;
    }
}
