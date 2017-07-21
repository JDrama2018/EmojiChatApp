//
//  VerificationCodeViewController.swift
//  EmijiChat
//
//  Created by LEE on 6/17/17.
//  Copyright © 2017 Meapp90. All rights reserved.
//

import UIKit
import SinchVerification

import Toast_Swift


class VerificationCodeViewController: UIViewController {

    
    @IBOutlet weak var Label_PhonenNmber: UILabel!
    @IBOutlet weak var Label_VerificationCode: UITextField!
    
    @IBOutlet weak var View_Dark: UIView!
    @IBOutlet weak var View_Resend: UIView!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
//        View_Dark.isHidden = true
//        View_Resend.isHidden = true
        
        
        //Number Pad
        Label_VerificationCode.keyboardType = .numberPad
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        
        Label_PhonenNmber.text = g_ProfileInfo.fullphone
//        View_Dark.isHidden = true
//        View_Resend.isHidden = true
    }
    
    
    
    @IBAction func onTappedBackButton(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }

    @IBAction func onTappedDontReceiveButton(_ sender: Any) {
//        View_Dark.isHidden = false
//        View_Resend.isHidden = false
        
        let alert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet);
        let resendAction = UIAlertAction(title: "Resend", style: .default) { (action) in
            self.onTappedResendButton(self)
        };
        let callAction = UIAlertAction(title: "Call", style: .default) { (action) in
            self.onTappedCallButton(self)
        };
        let cancelAction = UIAlertAction(title: "Cancel", style: .cancel, handler: nil)//[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:nil];
        
        alert.addAction(resendAction);
        alert.addAction(callAction);
        alert.addAction(cancelAction);
        
        
        present(alert, animated: true, completion: nil)
    }
    
    @IBAction func onTappedResendButton(_ sender: Any) {
        
        //Againg Resend
        Label_VerificationCode.text = ""
        
        
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
        
    }
    
    func onTappedCallButton(_ sender: Any) {
        
        //Againg Resend
        Label_VerificationCode.text = ""
        do {

            let phoneNumber = try SharedPhoneNumberUtil().parse(g_ProfileInfo.fullphone, defaultRegion:"");
            let phoneNumberE164 = SharedPhoneNumberUtil().format(phoneNumber, format: PhoneNumberFormat.e164);
            
            g_SinchVerification = CalloutVerification(g_SinchApplicationKey, phoneNumber: phoneNumberE164)
            
            g_SinchVerification.initiate { (result: InitiationResult, error:Error?) -> Void in
                
                if (result.success) {
                    // Show UI for entering the code which will be received via SMS
                    print("will be received via call")
                }
                if error != nil {
                    print(error?.localizedDescription)
                }
                
            }
        } catch {
            print("error");
        }
        
        
    }
    
    @IBAction func onTappedCancelButton(_ sender: Any) {
        View_Dark.isHidden = true
        View_Resend.isHidden = true
    }
    
    @IBAction func onTappedSubmitButton(_ sender: Any) {
        Label_VerificationCode.resignFirstResponder()
        if Label_VerificationCode.text == "" {
            return
        }
        
        
        ProgressHUD.show("Verifying...")
        let code = self.Label_VerificationCode.text!
        g_SinchVerification.verify(
            code, completion: { (success:Bool, error:Error?) -> Void in
                
                if (success) {
                    
                    ProgressHUD.dismiss()
                    print("Verified")
                    self.view.makeToast("your verified successfully.", duration: 2.0, position: .center)
                    
                    let when = DispatchTime.now() + 2 // change 2 to desired number of seconds
                    DispatchQueue.main.asyncAfter(deadline: when) {
                        // Your code with delay
                        self.performSegue(withIdentifier: StorySegues.FromVerificationCodeToCompleteProfile.rawValue, sender: self)
                    }
                } else {
                    
                    ProgressHUD.dismiss()
                    self.view.makeToast("sorry your verified not successfully.", duration: 3.0, position: .bottom)
                    print(error?.localizedDescription)
                }
        })
        
    }
    

}
