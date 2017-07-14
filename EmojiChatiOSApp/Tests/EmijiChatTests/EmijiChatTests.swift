import XCTest
@testable import EmijiChat

class EmijiChatTests: XCTestCase {
    func testExample() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        XCTAssertEqual(EmijiChat().text, "Hello, World!")
    }


    static var allTests : [(String, (EmijiChatTests) -> () throws -> Void)] {
        return [
            ("testExample", testExample),
        ]
    }
}
