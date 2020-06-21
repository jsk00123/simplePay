package org.jjolab.simplepay.common;

import org.jjolab.simplepay.utils.CryptoUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class CryptoUtilTest {

    @Test
    public void aesEncryptionTest() throws Exception {
        // given
        String plainText = "Hello, World!";
        String key = "secret key";

        // when
        String encrypted = CryptoUtil.encryptAES256(plainText, key);
        String decrypted = CryptoUtil.decryptAES256(encrypted, key);

        // then
        Assert.assertEquals(plainText, decrypted);
    }

}