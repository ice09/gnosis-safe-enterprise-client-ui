package tech.blockchainers.safe.web.rest.external;

import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.blockchainers.safe.web.rest.dto.GnosisSafeSetupDto;

@Service
public class ExternalGnosisSafeServices {

    @Value("${external.gnosis-safe.url}")
    private String gnosisSafeServices;

    public String getAddressForSaltedUser(String seed) {
        return Unirest
            .get(gnosisSafeServices + "/api/createKey")
            .queryString("seed", seed)
            .asJson()
            .getBody()
            .getObject()
            .getString("addressHex");
    }

    public String signTransactionWithSalt(String salt, String safeAddress, String tokenAddress, String to, Integer value) {
        return Unirest.get(gnosisSafeServices + "/api/signTransaction")
            .queryString("seed", salt)
            .queryString("gnosisSafeAddress", safeAddress)
            .queryString("tokenAddress", tokenAddress)
            .queryString("to", to)
            .queryString("value", value)
            .asJson()
            .getBody()
            .getObject()
            .getString("signature");
    }

    public String submitTransaction(String safeAddress, String tokenAddress, String to, Integer value, String addressesAndSignatures) {
//GET http://localhost:8888/api/sendTransaction?
// gnosisSafeAddress={{safeAddress}}&
// tokenAddress={{tokenAddress}}&
// to=0x0000000000000000000000000000000000000001&
// value=10&
// addressAndSignature={{ownerAddress1}};{{signature1}},{{ownerAddress2}};{{signature2}}
        return Unirest.get(gnosisSafeServices + "/api/sendTransaction")
            .queryString("gnosisSafeAddress", safeAddress)
            .queryString("tokenAddress", tokenAddress)
            .queryString("to", to)
            .queryString("value", value)
            .queryString("addressAndSignature", addressesAndSignatures)
            .asJson()
            .getBody()
            .getObject()
            .getString("transactionHash");
    }

    public String setupGnosisSafe(GnosisSafeSetupDto gnosisSafeSetupDto) {
        return Unirest.patch(gnosisSafeServices + "/api/setupSafe")
            .header("Content-Type", "application/json")
            .body(gnosisSafeSetupDto)
            .asJson()
            .getBody()
            .getObject()
            .getString("address");
    }

}
