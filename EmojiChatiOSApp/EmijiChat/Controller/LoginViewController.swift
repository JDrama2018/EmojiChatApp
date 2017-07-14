//
//  LoginViewController.swift
//  EmijiChat
//
//  Created by LEE on 6/20/17.
//  Copyright © 2017 Meapp90. All rights reserved.
//

import UIKit

//import CountryPicker
import SinchVerification

import FirebaseDatabase
//import FirebaseStorage
//import FirebaseAuth
//import SDWebImage
//import Toast_Swift

class LoginViewController: UIViewController, UITextFieldDelegate {
    
    //////////////////////////////////////////////////////////
    
    
    @IBOutlet weak var Label_Country: UILabel!
    @IBOutlet weak var Label_PhoneCode: UILabel!
    @IBOutlet weak var Text_PhoneNumber: UITextField!
    @IBOutlet weak var Text_Password: UITextField!
    
    @IBOutlet weak var Button_Login: UIButton!
    @IBOutlet weak var Button_ForgetPassword: UIButton!
    
    @IBOutlet weak var View_SelectCountry: UIView!
//    @IBOutlet weak var Country_Picker: CountryPicker!
    
    //////////////////////////////////////////////////////////
    
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //Init Country_Picker-> hidden
        View_SelectCountry.isHidden = true
        
        //get corrent country
        let locale = Locale.current
        let code = (locale as NSLocale).object(forKey: NSLocale.Key.countryCode) as! String?
        //init Picker
//        Country_Picker.countryPickerDelegate = self
//        Country_Picker.showPhoneNumbers = true
//        Country_Picker.setCountry(code!)
        
        //Number Pad
        Text_PhoneNumber.delegate = self
        Text_PhoneNumber.keyboardType = .numberPad
        
        // Do any additional setup after loading the view.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        //Init Country_Picker-> hidden
        View_SelectCountry.isHidden = true
    }
    
    
    // a picker item was selected
//    func countryPhoneCodePicker(_ picker: CountryPicker, didSelectCountryWithName name: String, countryCode: String, phoneCode: String, flag: UIImage) {
//        //pick up anythink
//        Label_PhoneCode.text = phoneCode
//        Label_Country.text = name
//    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool
    {
        /*if textField == self.Text_PhoneNumber {
         
         let invalidCharacters = CharacterSet(charactersIn: "0123456789").inverted
         return string.rangeOfCharacter(from: invalidCharacters, options: [], range: string.startIndex ..< string.endIndex) == nil
         }*/
        if textField == self.Text_PhoneNumber {
            
            let invalidCharacters = CharacterSet(charactersIn: "0123456789").inverted
            return string.rangeOfCharacter(from: invalidCharacters, options: [], range: string.startIndex ..< string.endIndex) == nil
        } else {
            let invalidCharacters = CharacterSet(charactersIn: "0123456789").inverted
            return string.rangeOfCharacter(from: invalidCharacters, options: [], range: string.startIndex ..< string.endIndex) == nil
        }
    }
    
    

    @IBAction func onTappedSelectCountryButton(_ sender: Any) {
        View_SelectCountry.isHidden = false
        Button_Login.isHidden = true
        Button_ForgetPassword.isHidden = true
    }
    
    @IBAction func onTappedDoneButton(_ sender: Any) {
        View_SelectCountry.isHidden = true
        Button_Login.isHidden = false
        Button_ForgetPassword.isHidden = false
    }
    
    @IBAction func onTappedLoginButton(_ sender: Any) {
        
        if self.Text_PhoneNumber.text == "" {
            self.view.makeToast("please insert your phone number.", duration: 2.0, position: .bottom)
            return
        }
        if self.Text_Password.text == "" {
            self.view.makeToast("please insert your password.", duration: 2.0, position: .bottom)
            return
        }
        if self.Text_Password.text?.isValidPassword == false {
            self.view.makeToast("your password is not correct, please insert password again correctly.", duration: 2.0, position: .bottom)
            return
        }
        
        g_ProfileInfo.fullphone = Label_PhoneCode.text! + Text_PhoneNumber.text!
        g_ProfileInfo.password = Text_Password.text!
        
        ProgressHUD.show("Login...")
        var handle: UInt = 0
        let Ref_users = FIRDatabase.database().reference().child("users")
        handle = Ref_users.observe(.value , with: { (snapshot: FIRDataSnapshot!) in
            
            if snapshot.exists() {
                Ref_users.removeObserver(withHandle: handle)
                
                var foundFlag: Bool = false
                for snap in snapshot.children {
                    let userSnap = snap as! FIRDataSnapshot
                    let uid = userSnap.key //the uid of each user
                    print(uid)
                    
                    if let dict = userSnap.value as?  NSDictionary {
                        
                        let phone = dict["phone"] as? String ?? ""
                        let password = dict["password"] as? String ?? ""
                        
                        let fullname = dict["fullname"] as? String ?? ""
                        let avatar = dict["avatar"] as? String ?? ""
                        
                        if phone == g_ProfileInfo.fullphone {
                            g_UID = uid
                        }
                        if phone == g_ProfileInfo.fullphone && password == g_ProfileInfo.password {
                            
                            foundFlag = true
                            g_ProfileInfo.fullphone = phone
                            g_ProfileInfo.password = password
                            g_ProfileInfo.fullname = fullname
                            g_ProfileInfo.avatarUrl = avatar
                            
                            g_UID = uid
                            print("ok Login")
                            
                            //////////////////////////////////////////////////////////////////////////
                            // Got to chat page
                            //////////////////////////////////////////////////////////////////////////
                            
                            self.performSegue(withIdentifier: StorySegues.FromLoginToMainTab.rawValue, sender: self)
                            
                            ProgressHUD.dismiss()
                            break
                            
                        }
                    }
                }
                
                if foundFlag == false {
                    ProgressHUD.dismiss()
                    self.view.makeToast("Forgot Password", duration: 2.0, position: .bottom)
                }
            }
            else {
                
                Ref_users.removeObserver(withHandle: handle)
                
                ProgressHUD.dismiss()
                self.view.makeToast("maybe you not have account yet.", duration: 2.0, position: .bottom)
            }
            
        })
    }

    @IBAction func onTappedForgetPassword(_ sender: Any) {
        
        /*if g_UID == "" {
            self.view.makeToast("you not have account yet.", duration: 2.0, position: .bottom)
            return
        }*/
        if self.Text_PhoneNumber.text == "" {
            self.view.makeToast("please insert your phone number.", duration: 2.0, position: .bottom)
            return
        }
        g_ProfileInfo.fullphone = Label_PhoneCode.text! + Text_PhoneNumber.text!
        
        
        ProgressHUD.show()
        var handle: UInt = 0
        let Ref_users = FIRDatabase.database().reference().child("users")
        handle = Ref_users.observe(.value , with: { (snapshot: FIRDataSnapshot!) in
            
            if snapshot.exists() {
                Ref_users.removeObserver(withHandle: handle)
                
                var foundFlag: Bool = false
                for snap in snapshot.children {
                    let userSnap = snap as! FIRDataSnapshot
                    let uid = userSnap.key //the uid of each user
                    print(uid)
                    
                    if let dict = userSnap.value as?  NSDictionary {
                        
                        let phone = dict["phone"] as? String ?? ""
                        let password = dict["password"] as? String ?? ""
                        let fullname = dict["fullname"] as? String ?? ""
                        let avatar = dict["avatar"] as? String ?? ""
                        
                        if phone == g_ProfileInfo.fullphone {
                            
                            foundFlag = true
                            ProgressHUD.dismiss()
                            g_UID = uid
                            
                            g_ProfileInfo.fullphone = phone
                            //g_ProfileInfo.password = password
                            g_ProfileInfo.fullname = fullname
                            g_ProfileInfo.avatarUrl = avatar
                            
                            
                                        // Send Verification Request.
                                        g_SinchVerification = SMSVerification(g_SinchApplicationKey, phoneNumber: g_ProfileInfo.fullphone)
                                        g_SinchVerification.initiate { (result: InitiationResult, error:Error?) -> Void in
                                            
                                            if (result.success) {
                                                // Show UI for entering the code which will be received via SMS
                                                print("will be received via SMS")
                                            }
                                            if error != nil {
                                                print(error?.localizedDescription)
                                            }
                                        }
                                        // Goto next Verification page.
                                        self.performSegue(withIdentifier: StorySegues.FromLoginToVerification_Login.rawValue, sender: self)
                            
                            break
                        }
                    }
                }
                
                if foundFlag == false {
                    ProgressHUD.dismiss()
                    
                    self.view.makeToast("maybe you not have account yet.", duration: 2.0, position: .bottom)
                }
            }
            else {
                
                Ref_users.removeObserver(withHandle: handle)
                ProgressHUD.dismiss()
                
                self.view.makeToast("maybe you not have account yet.", duration: 2.0, position: .bottom)
            }
        })
    }
    
    @IBAction func onTappedMenuCancelButton(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    
    
}
