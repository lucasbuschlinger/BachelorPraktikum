# ODVImporter
Version: 0.0.1
Classification: Importer

Overview
-----
The ODVImporter is an importer plugin which imports data from an ODV ZIP-Archive. 

Configuration
-----
The ODVImporter plugin offers the following configuration options:

| key  | value | description | required |
| ------------- | ------------- |  ------------- | ------------- |
| temporaryDirectory | "/tmp/odv/" | The temporary directory to use. | 
| metaFile | "meta-2.info" | The name of the meta file to use. (Default: meta.info) | 

Required Plugins
-----
 - The ODVImporter requires a list of plugins which are implicitly set by the data to import. 