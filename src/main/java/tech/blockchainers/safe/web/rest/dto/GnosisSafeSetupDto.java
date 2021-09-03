package tech.blockchainers.safe.web.rest.dto;

public class GnosisSafeSetupDto {

    String safeAddress;
    String[] owners;
    int threshold;

    public String getSafeAddress() {
        return safeAddress;
    }

    public void setSafeAddress(String safeAddress) {
        this.safeAddress = safeAddress;
    }

    public String[] getOwners() {
        return owners;
    }

    public void setOwners(String[] owners) {
        this.owners = owners;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
