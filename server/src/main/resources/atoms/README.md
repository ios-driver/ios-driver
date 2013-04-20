Add this gist to the selenium project file: javascript/atoms/build.desc

https://gist.github.com/nicegraham/11175810cb4e15af6047

then add the following to Rakefile

https://gist.github.com/nicegraham/e083dd02e35a4a744b50

(also this is available here https://github.com/lukeis/selenium/tree/ios-driver )

in the selenium project run the command:

    ./go ios_driver

then copy the built artifacts here:

    cp build/javascript/atoms/*_ios.js <path-to-ios-driver>/server/src/main/resources/atoms

