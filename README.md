ios-driver
==========


IOS native, hybrid and web automation

doc : http://ios-driver.github.com/ios-driver/


Contact
------------
Most of discussions take place via IRC at freenode on the *ios-driver* channel


Contributing
------------

Active development is done on the 'dev' branch. Please make use that branch and make pull requests to it instead of to master.

Please sign the CLA ["Personal"](https://docs.google.com/forms/d/1Bt0oyAX33lmEKRktPTZEDzpnSHsDd_cztwNPaKUxqD0/viewform), ["Corporate"](https://docs.google.com/forms/d/1btYM6nwpnSZFg_y6_O3Bg9EeqGBcvgfLCgp5SyBEZgw/viewform).

The ["release notes"](https://github.com/ios-driver/ios-driver/blob/master/release.notes) contain a list of TODOs at the bottom.

Building from source :
-----------------------

clone the project

clone the submobule ( git submodule init, git submodule update )

switch to the dev branch

build without the tests : mvn package -DskipTests

The server standalone will be in project/server/target


  
License
-----------
[The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
