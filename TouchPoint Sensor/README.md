### ValidIS touchpoint sensor

The ValidIS touhcpoint sensor forms the local component of the architecture. It exposes an API for systems to interact with. It is used to interact with the OSCAR database which uses MySQL.

The ValidIS touchpoint calculates various data rates and sends them for aggregation when asked by the Hub.

#### Usage

For startup you can use the `startup.rb` script which automatically adds the given CIS details into the Hub database which can be used later, by other users for adding this into their networks for monitoring.

#### Documentation

There is not much documentation at this point in the project. But you can find method ducmentation [here](http://chinmaydd.github.io/tp-sensor/).

#### License

Copyright Chinmay Deshpande and Jens H. Weber, 2016.
