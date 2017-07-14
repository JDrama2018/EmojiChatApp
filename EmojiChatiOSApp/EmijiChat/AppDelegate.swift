//
//  AppDelegate.swift
//  EmijiChat
//
//  Created by LEE on 6/17/17.
//  Copyright © 2017 Meapp90. All rights reserved.
//

import UIKit
import Firebase
//import GoogleMobileAds



@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?


    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        window?.tintColor = UIColor(colorLiteralRed: 98.0/255, green: 211.0/255, blue: 82.0/255, alpha: 1)
        //GADMobileAds.configure(withApplicationID: "ca-app-pub-8501671653071605~9497926137")
        
        // Initialize the Google Mobile Ads SDK.
        GADMobileAds.configure(withApplicationID: "ca-app-pub-8501671653071605/1974659335")
        
        //Google
        FIRApp.configure()
        
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.tokenRefreshNotification),
                                               name: NSNotification.Name.firInstanceIDTokenRefresh,
                                               object: nil)
        
        
        return true
    }

    func tokenRefreshNotification(notification: NSNotification) {
        // NOTE: It can be nil here
        let refreshedToken = FIRInstanceID.instanceID().token()
        print("InstanceID token: \(refreshedToken)")
        
        connectToFcm()
    }
    func connectToFcm() {
        
        FIRMessaging.messaging().connect { (error) in
            
            if (error != nil) {
                print("Unable to connect with FCM. \(error)")
            } else {
                print("Connected to FCM.")
            }
        }
    }
    
    
    
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        
        
        FIRInstanceID.instanceID().setAPNSToken(deviceToken, type: FIRInstanceIDAPNSTokenType.sandbox)
        FIRInstanceID.instanceID().setAPNSToken(deviceToken, type: FIRInstanceIDAPNSTokenType.prod)
        
    
        print("============================================================")
        print(FIRInstanceID.instanceID().token())
        print("============================================================")
        
        var token = ""
        for i in 0..<deviceToken.count {
            token = token + String(format: "%02.2hhx", arguments: [deviceToken[i]])
        }
        
        print("============================================================")
        print(token)
        print("============================================================")
    }
    
    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print(error)
    }
    
    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any], fetchCompletionHandler completionHandler: (UIBackgroundFetchResult) -> Void) {
        
        // Print message ID.
        if let messageID = userInfo["gcm.message_id"] {
            print("Message ID: \(messageID)")               //Message ID: 0:1495466163558714%232f7941232f7941
        }
        // Print full message.
        print(userInfo)
        
        
        
        
        UIApplication.shared.applicationIconBadgeNumber = 0
        print(userInfo)
        
        completionHandler(UIBackgroundFetchResult.newData)
    }
    
    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }


}

// [START ios_10_data_message_handling]
/*extension AppDelegate : FIRMessagingDelegate {
    // Receive data message on iOS 10 devices while app is in the foreground.
    func applicationReceivedRemoteMessage(_ remoteMessage: FIRMessagingRemoteMessage) {
        print(remoteMessage.appData)
    }
}*/
// [END ios_10_data_message_handling]
