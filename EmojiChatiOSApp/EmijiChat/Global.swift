//
//  Global.swift
//  Amoureuse
//
//  Created by LEE on 3/30/17.
//  Copyright © 2017 LEE. All rights reserved.
//

import Foundation
import UIKit
import SinchVerification

var g_SinchVerification: Verification!
var g_SinchApplicationKey = "190ebe1c-31c5-4d69-9d36-1e5b7a720454";

// Firebase
var g_UID: String = ""


struct ProfileInfo{
    var fullphone:      String
    var fullname:       String
    var password:       String
    var avatarImage:    UIImage
    var avatarUrl:      String
}

var g_ProfileInfo: ProfileInfo = ProfileInfo(
 
    fullphone: "+8615940337090",
    fullname: "alex wallet",
    password: "12345678",
    avatarImage: #imageLiteral(resourceName: "3_userProfile.png"),      // 3_userProfile.png
    avatarUrl:  "https://firebasestorage.googleapis.com/v0/b/emojichat-e222b.appspot.com/o/user_avatars%2F10E39F00-52C8-49E5-BBC0-EB3B6535E5A7%2Favatar.jpg?alt=media&token=ab2ba07d-efd9-45da-b19f-c4cbcfc8fab2"
)


struct contact_infor{
    var udid:           String
    var fullphone:      String
    var fullname:       String
    var avatarUrl:      String    
    var created:        String
}

var g_contact_Array:    Array<contact_infor> = Array<contact_infor>()





