//import PackageDescription
//
//let package = Package(
//    name: "EmijiChat"
//)

import PackageDescription

let package = Package(
    name: "EmijiChat",      //"YOUR_PROJECT_NAME",
    targets: [],
    dependencies: [
        .Package(url: "git@github.com:4taras4/CountryCode.git")
    ]
)


import PackageDescription

let package = Package(
    name: "CountryCode"
)
