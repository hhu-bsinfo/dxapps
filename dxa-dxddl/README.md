# DXApp DXDdl example and template
This application shows how to use the Data-Description-Language (DXDdl) for DXRAM and is based on the master-slave
example wich is described [here](https://github.com/hhu-bsinfo/dxapps/tree/development/dxa-masterslave). 
In this example the chained lists are realized using DXDdl including direct memory access.

## Compiling
The application requires DXRAM and DXDdl. 

The description below assumes following directory structure.
```
 ~/dxram
 ~/dxddl
 ~/dxapps
 ```

1. Get and compile [DXRAM](https://github.com/hhu-bsinfo/dxram/) (development version)

2. Get and compile [DXDdl](https://github.com/hhu-bsinfo/dxddl/) (development version)

3. Get the [DXApps](https://github.com/hhu-bsinfo/dxapps)

3. Get and generate the `dxddl-api.jar` for the `Node.dxddl` file enclosed in this example.
   Copy the resulting output file `dxddl-api.jar` to the `libs` directory of this example.

4. Compile this application by executing the `build.sh` script. The output jar-file will be 
located within `~/build/libs/dxa-dxddl.jar`.

## Deployment and running an application
See master-slave example.

Further documentation on how to develop applications for DXRAM can be
found in the docs of the [DXRAM repository](https://github.com/hhu-bsinfo/dxram/).

# License
Copyright (C) 2019 Heinrich-Heine-Universitaet Duesseldorf, Institute of Computer Science, Department Operating Systems.
Licensed under the [GNU General Public License](LICENSE.md).
