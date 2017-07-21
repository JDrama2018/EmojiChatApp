//
//  SignupViewController.swift
//  EmijiChat
//
//  Created by LEE on 6/17/17.
//  Copyright © 2017 Meapp90. All rights reserved.
//

import UIKit

//import CountryPicker
import SinchVerification
import FirebaseDatabase


import CryptoSwift

class SignupViewController: UIViewController, UITextFieldDelegate {

    //////////////////////////////////////////////////////////
    @IBOutlet weak var View_Dark: UIView!
    @IBOutlet weak var View_Confirmation: UIView!
    
    
    @IBOutlet weak var Label_Country: UILabel!
    @IBOutlet weak var Label_PhoneCode: UILabel!
    @IBOutlet weak var Text_PhoneNumber: UITextField!
    @IBOutlet weak var Text_Password: UITextField!
    
    @IBOutlet weak var Button_Signup: UIButton!
    
    @IBOutlet weak var View_SelectCountry: UIView!
//    @IBOutlet weak var Country_Picker: CountryPicker!
    
    @IBOutlet weak var Label_FullPhoneNumber: UILabel!
    
    //////////////////////////////////////////////////////////
    
    var isoCountryCode: String!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        /*
        let inputBytes: [UInt8] = Array("secret".utf8)
        let key:        [UInt8] = Array("secret0key000000".utf8) //16
        let iv:         [UInt8] = Array("0000000000000000".utf8)  //16
        
        var encryptedBase64 = ""
        do {
            
            let encrypted: [UInt8] = try AES(key: key, iv: iv, blockMode: .CBC, padding: PKCS7()).encrypt(inputBytes)
            
            
            let encryptedNSData = NSData(bytes: encrypted, length: encrypted.count)
            encryptedBase64 = encryptedNSData.base64EncodedString(options: [])
            
            let decrypted: [UInt8] = try AES(key: key, iv: iv, blockMode: .CBC, padding: PKCS7()).decrypt(encrypted)
            let result = String(bytes: decrypted, encoding: String.Encoding.utf8)!
            print("result\t\(result )")
        } catch {
            // some error
        }
        print("encryptedBase64: \(encryptedBase64)")
        
        */
        
        
        // Create sample data
        /*let data = encryptedBase64.data(using: .utf8)!
        
        let encryptedData = NSData(base64Encoded: encryptedBase64, options:[])!
        print("decodedData: \(encryptedData)")
        
        
        let encrypted = data.withUnsafeBytes {
            [UInt32](UnsafeBufferPointer(start: $0, count: data.count))
        }
        
        
        do {
            
            let decryptedData = try AES(key: key, iv: iv, blockMode: .CBC, padding: PKCS7()).decrypt(encrypted)
            //let decryptedData = try AES(key: key, iv: iv, blockMode: .CBC).decrypt(encrypted)
            
            let decryptedString = String(bytes: decryptedData, encoding: NSUTF8StringEncoding)!
            print("decryptedString: \(decryptedString)")
            
        }
        catch{
            // some error
        }*/
        
        
        
        
        
        
        
        
        
        
        
//        View_Confirmation.layer.cornerRadius = 8.0
        
//        View_Dark.isHidden = true
//        View_Confirmation.isHidden = true
        
        //Init Country_Picker-> hidden
//        View_SelectCountry.isHidden = true
        
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
    
    // a country was selected
    func countrySelected(countryCode: String) {
        //pick up anythink
        
        let regions = SharedPhoneNumberUtil().regionList(forLocale: Locale.current);
        let displayName = regions.displayName(forRegion: isoCountryCode);
        let callingCode = regions.countryCallingCode(forRegion: isoCountryCode);
        
        Label_PhoneCode.text = String(format:"+%@", callingCode!);
        Label_Country.text = displayName;
    }
    
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
//        View_SelectCountry.isHidden = false
//        Button_Signup.isHidden = true
    }

    @IBAction func onTappedDoneButton(_ sender: Any) {
        View_SelectCountry.isHidden = true
        Button_Signup.isHidden = false
    }
    
    @IBAction func onTappedSignupButton(_ sender: Any) {
        
        if self.Text_PhoneNumber.text == "" {
            self.view.makeToast("please insert your phone number.", duration: 2.0, position: .bottom)
            return
        }
        
        
        g_ProfileInfo.fullphone = self.Label_PhoneCode.text! + Text_PhoneNumber.text!

        let alert = UIAlertController(title: "Mobile Number Confirmation", message: String(format:"We will send a verification code to the following number: %@",self.Label_PhoneCode.text! + Text_PhoneNumber.text!), preferredStyle: .alert);//[UIAlertController alertControllerWithTitle:@"Mobile Number Confirmation" message:[NSString stringWithFormat:@"We will send a verification code to the following number: %@",number] preferredStyle:UIAlertControllerStyleAlert];
        let okAction = UIAlertAction(title: "Ok", style: .default) { (action) in
            self.sendVerificationCode()
        };//[UIAlertAction actionWithTitle:@"Ok" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action){
        
        let cancelAction = UIAlertAction(title: "Cancel", style: .cancel, handler: nil)//[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:nil];
        
        alert.addAction(okAction);
        alert.addAction(cancelAction);
        
        
        present(alert, animated: true, completion: nil)
    }
    
    @IBAction func onTappedMenuCancelButton(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func onTappedConfirmationCancelButton(_ sender: Any) {
        View_Dark.isHidden = true
        View_Confirmation.isHidden = true
    }
    
    func sendVerificationCode() {
        Text_PhoneNumber.resignFirstResponder()
        ProgressHUD.show()

        print(g_ProfileInfo.fullphone)

        
        g_SinchVerification = SMSVerification(g_SinchApplicationKey, phoneNumber: g_ProfileInfo.fullphone)
        g_SinchVerification.initiate { (result: InitiationResult, error:Error?) -> Void in
            ProgressHUD.dismiss()

            if (result.success) {
                print("will be received via SMS")
            }
            if error != nil {
                print(error?.localizedDescription)
            }
        }

        self.performSegue(withIdentifier: StorySegues.FromSignupToVerificationCode.rawValue, sender: self)
        
        
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let controller = (segue.destination as? UINavigationController)?.childViewControllers.first as? CountrySelectionViewController {
            controller.isoCountryCode = isoCountryCode;
            controller.onCompletion = {(selectedCountryCode: String) -> Void in
                controller.dismiss(animated: true, completion: nil);
                self.isoCountryCode = selectedCountryCode;
                self.countrySelected(countryCode: selectedCountryCode);
            }
        }
    }
}
