Add this gist to the selenium project file: javascript/atoms/build.desc

https://gist.github.com/lukeis/01c18be14eb81b17c932

then add the following to Rakefile

https://gist.github.com/lukeis/25869b3e28cdd3d80ecb

(also this is available here https://github.com/lukeis/selenium/tree/ios-driver )

in the selenium project run the command:

    ./go ios_driver

then copy the built artifacts here:

    cp build/javascript/atoms/*_ios.js <path-to-ios-driver>/server/src/main/resources/atoms

