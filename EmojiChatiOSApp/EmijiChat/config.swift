//
//  Config.swift
//  Onefortheride
//
//  Created by mac on 25/11/15.
//  Copyright © 2015 mac. All rights reserved.
//

import Foundation
/*
 enum ProfileViewIndex: Int {
 case RESTAURANTS    = 0
 case SEARCHES       = 1
 case FRIENDS        = 2
 case REWARDS        = 3
 }*/

enum StorySegues: String {
    //case FromFirstMainToSignup          = "FirstMainToSignup"
    case FromFirstMainToSign            =   "FirstMainToSign"
    case FromSignToSignup               =   "SignToSignup"
    case FromSignToSignin               =   "SignToSignin"
    case FromSignToLogin                =   "SignToLogin"
    case FromLoginToVerification_Login  =   "LoginToVerification_Login"
    case FromVerification_LoginToSetNewPassword =   "Verification_LoginToSetNewPassword"
    case FromSignupToVerificationCode   =   "SignupToVerificationCode"
    case FromVerificationCodeToCompleteProfile  =   "VerificationCodeToCompleteProfile"
    
    case FromCompleteProfileToMainTab   =   "CompleteProfileToMainTab"
    case FromSetNewPasswordToMainTab    =   "SetNewPasswordToMainTab"
    case FromLoginToMainTab             =   "LoginToMainTab"

    

}

enum UserDialogs: String {
    /*case SigninIncorrect        = "Your login information is incorrect."
     case EmailIsTaken           = "That email address is already taken."*/
    case CompleteRequireFields  = "Please complete all required fields."
    /*case RequireEmailAddress    = "Email address should be required."
     case PasswordRecoveryFailed = "Recovery is failed. Try again."*/
    case PasswordNotMatch       = "Passwords do not match"
    case FacebookSignOK       = "success Login with Facebook"
    case GoogleSignOK       = "success Login with Google"
}
