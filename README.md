# Read Me First
This service analyses receipt photo and returns detailed pricing information.
The main goal of this service is to calculate the price of single item on receipt
eliminating need for manual calculator use.
This backend service has frontend application kameleon-receipt-reader-frontend.

### Technology stack
1. Spring Boot.
2. Spring Security.
3. Spring Framework.
4. Maven.

### External services used
1. Google Auth.
2. Google Vision.
3. ChatGPT.

### Setup
1. Setup `GOOGLE_APPLICATION_CREDENTIALS` environment variable to point to your private key json.
Download from your service account, "Manage keys" option.
2. Setup `GOOGLE_CLIENT_SECRET` environment variable, which you will receive during your Google Authentication setup.
3. Setup `OPEN_API_KEY` which is your private ChatGPT API key.
4. Modify `kameleon.redirect.url` property if you want to deploy this app to prod. This is your redirect after
Google authentication URL.

### Spring Profiles
1. Standard, no name, which specifies http://localhost:4200 as redirect URL after authentication.
2. Prod, which specifies https://angular-test-970a4.web.app as redirect URL after authentication.

### How to build
```shell
mvn clean install
```

