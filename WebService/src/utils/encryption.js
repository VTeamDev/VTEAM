'use strict';

const crypto = require('crypto');
const algorithm = 'aes-256-ctr';

module.exports = function(encryptionPassword) {
    const self = this;

    self.password = encryptionPassword;

    self.encrypt = encrypt;
    self.decrypt = decrypt;

    function encrypt(text) {
        var cipher = crypto.createCipher(algorithm, self.password)
        var crypted = cipher.update(text,'utf8','hex')
        crypted += cipher.final('hex');
        return crypted;
    }

    function decrypt(text) {
        var decipher = crypto.createDecipher(algorithm, self.password)
        var dec = decipher.update(text,'hex','utf8')
        dec += decipher.final('utf8');
        return dec;
    }
};
