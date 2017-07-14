//
//  SetNewPasswordViewController.swift
//  EmijiChat
//
//  Created by LEE on 6/20/17.
//  Copyright © 2017 Meapp90. All rights reserved.
//

import UIKit
import FirebaseDatabase
import FirebaseStorage
import FirebaseAuth
//import SDWebImage
import Toast_Swift

class SetNewPasswordViewController: UIViewController {

    @IBOutlet weak var Text_NewPassword: UITextField!
    @IBOutlet weak var Text_Confirm: UITextField!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @IBAction func onTappedBackButton(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func onTappedNextButton(_ sender: Any) {
        // goto chat page
        
        if Text_NewPassword.text == "" {
            self.view.makeToast("please insert new password.", duration: 2.0, position: .bottom)
            return
        }
        if Text_Confirm.text == "" {
            self.view.makeToast("please insert confirm.", duration: 2.0, position: .bottom)
            return
        }
        if Text_NewPassword.text != Text_Confirm.text {
            self.view.makeToast("not march password and confirm.", duration: 2.0, position: .bottom)
            return
        }
        if self.Text_NewPassword.text?.isValidPassword == false {
            self.view.makeToast("your password is not correct, please insert password again correctly.", duration: 2.0, position: .bottom)
            return
        }
        if g_UID == "" {
            self.view.makeToast("you not have account.", duration: 2.0, position: .bottom)
            return
        }
        
        g_ProfileInfo.password = Text_NewPassword.text!
        
        DispatchQueue.main.async {
            
            let newMessage_Ref = FIRDatabase.database().reference().child("users").child(g_UID)
            
            let messageData = ["createdAt": [".sv": "timestamp"],
                               "fullname": g_ProfileInfo.fullname,
                               "avatar": g_ProfileInfo.avatarUrl,
                               "phone": g_ProfileInfo.fullphone,
                               "password": g_ProfileInfo.password] as [String : Any]
           newMessage_Ref.setValue(messageData)
            
           self.view.makeToast("password successfully chaged.", duration: 2.0, position: .bottom)
           
            
            //////////////////////////////////////////////////////////////////////////////////////
            //
            // Go to Chat page.
            //
            //////////////////////////////////////////////////////////////////////////////////////
            self.performSegue(withIdentifier: StorySegues.FromSetNewPasswordToMainTab.rawValue, sender: self)
            
        }

        
    }
}
