//
//  CompleteProfileViewController.swift
//  EmijiChat
//
//  Created by LEE on 6/17/17.
//  Copyright © 2017 Meapp90. All rights reserved.
//

import UIKit

import FirebaseDatabase
import FirebaseStorage
import FirebaseAuth
//import SDWebImage
import Toast_Swift


class CompleteProfileViewController: UIViewController, WDImagePickerDelegate, UIImagePickerControllerDelegate {

    @IBOutlet weak var Btn_AvataImage_1: UIButton!
    @IBOutlet weak var Btn_AvataImage_2: UIButton!
    
    @IBOutlet weak var fullname_Textfield: UITextField!
    
    @IBOutlet weak var imageView_check: UIImageView!
    
    
    //WDImagePicker
    var imagePicker: WDImagePicker!
    var fixedImage: UIImage?
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
        fullname_Textfield.addTarget(self, action: #selector(textFieldDidChange(_:)), for: .editingChanged)
    }

    override func viewWillAppear(_ animated: Bool) {
        if fullname_Textfield.text != "" {
            imageView_check.image = #imageLiteral(resourceName: "3_fullname_check.png")
        } else {
            imageView_check.image = nil
        }
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func textFieldDidChange(_ textField: UITextField) {
        if (fullname_Textfield.text?.isEmpty)! {
            imageView_check.image = nil
        }
        else {
            imageView_check.image = #imageLiteral(resourceName: "3_fullname_check.png")
        }
    }

    
    @IBAction func onTappedBackButton(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }

    func imageWithImage(image: UIImage, newSize: CGSize) -> UIImage{
        UIGraphicsBeginImageContext(newSize)
        image.draw(in: CGRect(x: 0, y: 0, width: newSize.width, height: newSize.height))
        let newImage: UIImage = UIGraphicsGetImageFromCurrentImageContext()!
        UIGraphicsEndImageContext()
        return newImage
    }
    
    @IBAction func onTappedAddAvatarImageButton(_ sender: Any) {
        
        g_ProfileInfo.fullname = self.fullname_Textfield.text!
        
        imagePicker = WDImagePicker()
        imagePicker.cropSize = CGSize(width: 375, height: 375)
        imagePicker.delegate = self
        let alert = UIAlertController(title: "option", message: nil, preferredStyle: .actionSheet)
        alert.addAction(UIAlertAction(title: "Take Photo", style: .default, handler:{
            action in
            self.imagePicker.imagePickerController.sourceType = .camera
            self.present(self.imagePicker.imagePickerController, animated: true, completion: nil)
        }))
        alert.addAction(UIAlertAction(title: "Choose from Album", style: .default, handler: {
            action in
            self.imagePicker.imagePickerController.sourceType = .photoLibrary
            self.present(self.imagePicker.imagePickerController, animated: true, completion: nil)
        }))
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
        self.present(alert, animated: true, completion: nil)
    }
    func imagePicker(_ imagePicker: WDImagePicker, pickedImage: UIImage) {
        
        
        self.fixedImage = self.imageWithImage(image: pickedImage, newSize: CGSize(width: 80, height: 80))
        //self.fixedImage = Utils.profileImage(image: self.fixedImage!)
        
        g_ProfileInfo.avatarImage = fixedImage!
        self.Btn_AvataImage_2.setBackgroundImage(fixedImage, for: .normal)
        
        self.fullname_Textfield.text = g_ProfileInfo.fullname

        
        self.imagePicker.imagePickerController.dismiss(animated: true, completion: nil)
        
        func imagePickerController(_ picker: UIImagePickerController, didFinishPickingImage image: UIImage!, editingInfo:[AnyHashable: Any]!)
        {
            picker.dismiss(animated: true, completion: nil)
        }
    }
    

    //============================================================================================
    @IBAction func onTappedGotoChatPage(_ sender: Any) {
        
        if self.fullname_Textfield.text == "" {
            self.view.makeToast("please insert your full name.", duration: 2.0, position: .bottom)
            return
        }
        g_ProfileInfo.fullname = self.fullname_Textfield.text!
 
        // First Authentication ===============
        /*ProgressHUD.show("Signup...")
        FIRAuth.auth()?.signIn(withEmail: "elizhang221@gmail.com", password: "elizhang221") { (user, error) in
            
            if error == nil {
                
                self.UploadAvatarImage()
            } else {
                ProgressHUD.dismiss()
                self.view.makeToast("Network is busy now.", duration: 2.0, position: .bottom)
            }
        }*/
        
        // Second Authentication ===============
        ProgressHUD.show("Signup...")
        self.UploadAvatarImage()
    }
    
    func UploadAvatarImage(){
        
        //self.downcount = 0
        
        let sticks = UUID().uuidString
        print(sticks)
        
        downloadedFrom(image: self.imageWithImage(image: g_ProfileInfo.avatarImage, newSize: CGSize(width: 80, height: 80)), filePath: "user_avatars/\(sticks)/avatar.jpg", completion: { downloadUrl in
            
            g_ProfileInfo.avatarUrl = downloadUrl
            
            //==============================================================================================
            //self.downCompleted()
            self.GetUID(completion: {
                self.SignUp()
            })
            //==============================================================================================
        })
    }
    func downloadedFrom(image: UIImage, filePath: String, completion: @escaping (String) -> Void){
        
        var data: NSData = NSData()
        data = UIImageJPEGRepresentation(image, 0.1)! as NSData
        let metadata = FIRStorageMetadata()
        metadata.contentType = "image/jpg"
        
        var DownloadUrl: String = ""
        
        FIRStorage.storage().reference().child(filePath).put(data as Data , metadata: metadata, completion: { (metadata, error) in
            
            if error == nil {
                //self.downcount += 1
                DownloadUrl = (metadata?.downloadURLs?[0].absoluteString)!
                completion(DownloadUrl)
            }
        })
    }
    func GetUID(completion: @escaping() -> Void) {
        
        var handle: UInt = 0
        let Ref_users = FIRDatabase.database().reference().child("users")
        handle = Ref_users.observe(.value , with: { (snapshot: FIRDataSnapshot!) in
            
            if snapshot.exists() {
                
                Ref_users.removeObserver(withHandle: handle)
                
                print("users snapshot exists")
                print(snapshot.childrenCount)
                
                g_UID = String(snapshot.childrenCount + 1)
                completion()
                
            } else {
                
                Ref_users.removeObserver(withHandle: handle)
                
                print("users snapshot doesn't exists")
                print(snapshot.childrenCount)
                
                g_UID = String(snapshot.childrenCount + 1)
                completion()
                
            }
        })
        
    }
    func SignUp() {
        DispatchQueue.main.async {
            
            let newMessage_Ref = FIRDatabase.database().reference().child("users").child(g_UID)
            
            let messageData = ["createdAt": [".sv": "timestamp"],
                               "fullname": g_ProfileInfo.fullname,
                               "avatar": g_ProfileInfo.avatarUrl,
                               "phone": g_ProfileInfo.fullphone,
                               "password": g_ProfileInfo.password] as [String : Any]
            newMessage_Ref.setValue(messageData)
            
            ProgressHUD.dismiss()
            
            //////////////////////////////////////////////////////////////////////////////////////
            //
            // Go to Chat page.
            //
            //////////////////////////////////////////////////////////////////////////////////////
            self.performSegue(withIdentifier: StorySegues.FromCompleteProfileToMainTab.rawValue, sender: self)
        }
    }
        
}
