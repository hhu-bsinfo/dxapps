# DXTerm

[![Build Status](https://travis-ci.org/hhu-bsinfo/dxterm.svg?branch=master)](https://travis-ci.org/hhu-bsinfo/dxterm)
[![Build Status](https://travis-ci.org/hhu-bsinfo/dxterm.svg?branch=development)](https://travis-ci.org/hhu-bsinfo/dxterm)

[DXRam](https://github.com/hhu-bsinfo/dxram)'s terminal client application

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

  - Java SE Development Kit 8 - [Download](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

### Installing

```sh
git clone git@github.com:hhu-bsinfo/dxterm.git

cd dxterm

./gradlew client:installDist
```

After those steps `client/build/install` contains a DXTerm distribution.

## Running the application

```sh
cd client/build/install/client/bin

./client <DXRam-IP>
```


## Running the tests

```sh
./gradlew clean test
```

## License

This project is licensed under the GPLv3 License - see the [LICENSE](LICENSE) file for details
