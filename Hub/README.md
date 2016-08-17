### ValidIS Hub

The ValidIS Hub forms the cloud based component of the entire architecture. It is a REST based API system which talks to the backend MongoDB database using a library called `monger`.

It is built on top of [Authenticated Compojure API](https://github.com/JarrodCTaylor/authenticated-compojure-api) by Jarrod Taylor but aims to serve as an example of a Compojure application.

#### Usage

1. Make sure you have all the dependencies for Leininghen and Clojure installed.

2. Add a `profiles.clj` file containing the following content:
```
{:dev-env-vars  {:env {:auth-key "SecretKey"}}}
```
This will be used to sign your API tokens. Each token is valid for 15 minutes. Refresh token feature will be added soon.

3. Email based authentication feature is disabled by default. If you want to wish to add it, please make sure to add your email and password to the `profiles.clj` file and use it from there.

#### API Documentation

You can find the API documentation [here](https://chinmaydd.github.io/validis-hub).

### Other documentation

You can head over [here](https://chinmaydd.github.io/validis-hub) for the entire documentation.

### Run the application locally

`lein run [port]`

### Run the tests

Test coverage will be updated soon. Tests are written for almost every single core route function except for `get-network-information` since we do not have that many CIS instances running simultaneously.

`lein test`

#### License

Copyright Chinmay Deshpande and Jens H. Weber, 2016.
