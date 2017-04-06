# MasterKey
An droid application to secure all passwords.

### Initial Setup
After pull, first update (your own choice of) Digestor and Encyptor configurations in /assets/app.properties file.
*For example*,

### Properties of Digester
DIGESTER_ALGORITHM=MD5
ITERATIONS=1000

### Properties of Encryptor
ENCRYPTOR_ALGORITHM=PBEWITHMD5ANDTRIPLEDES
SALT_PWD=SALT
