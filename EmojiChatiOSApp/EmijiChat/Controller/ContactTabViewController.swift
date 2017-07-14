//
//  ContactTabViewController.swift
//  EmijiChat
//
//  Created by LEE on 6/20/17.
//  Copyright © 2017 Meapp90. All rights reserved.
//

import UIKit
import GoogleMobileAds

import FirebaseDatabase
import SDWebImage


class ContactTabViewController: UIViewController, GADBannerViewDelegate, UITableViewDelegate, UITableViewDataSource {

    //Banner
    @IBOutlet weak var GoogleBannerView: GADBannerView!    
    
    @IBOutlet weak var tableView: UITableView!
    
    
    
    //Set Table Refresh Control
    var refreshControl = UIRefreshControl()
    
    
    
    //var words = ["aa","ab","ac", "cc","cc", "dd","ee", "hh", "oo","nn", "mm"]
    //var words = [String]()
    var words = [contact_infor]()
    var wordsSection = [String]()
    var wordsDict = [String: [contact_infor]]()
    
    var wordsIndexTitles = ["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"]
        //["a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"]

    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        generateWordsDict()
        
        
        //Set Delegate Table View
        tableView.tableHeaderView = UIView()
        tableView.delegate = self
        tableView.dataSource = self
        
        //Set Table Refresh Control
        refreshControl = UIRefreshControl()
        refreshControl.attributedTitle = NSAttributedString(string: "Pull to refresh")
        refreshControl.addTarget(self, action: #selector(self.handleRefresh(sender:)), for: UIControlEvents.valueChanged)
        self.tableView.addSubview(refreshControl)
        
        handleRefresh(sender: self)
        
        
        initAdMobBanner()
        
        
    }

    //=====================================================================================
    func handleRefresh(sender:AnyObject) {
        
        tryGet_contact(completion: {
            self.refreshControl.endRefreshing()
            //self.tableView.reloadData()
        })
    }
    
    func tryGet_contact(completion: @escaping() -> Void) {
        
        
        ProgressHUD.show("updating...")
        var handle: UInt = 0
        //let Ref_users = FIRDatabase.database().reference().child("users")
        //handle = Ref_users.observe(.value , with: { (snapshot: FIRDataSnapshot!) in
        
        let Ref_users = FIRDatabase.database().reference().child("users")
        handle = Ref_users.queryOrdered(byChild: "fullname").observe(.value, with: { (snapshot: FIRDataSnapshot!) in
            
            if snapshot.exists() {
                Ref_users.removeObserver(withHandle: handle)
                
                
                g_contact_Array.removeAll()
                
                self.words.removeAll()
                self.wordsSection.removeAll()
                self.wordsDict.removeAll()
                
                
                for snap in snapshot.children {
                    let userSnap = snap as! FIRDataSnapshot
                    let uid = userSnap.key //the uid of each user
                    print(uid)
                    
                    if let dict = userSnap.value as?  NSDictionary {
                        
                        if g_UID != uid {
                            let phone    = dict["phone"]    as? String ?? ""
                            let fullname = dict["fullname"] as? String ?? ""
                            let avatar   = dict["avatar"]   as? String ?? ""
                            
                            let createdAt = dict["createdAt"] as! Double
                            print(createdAt)
                            
                            let dateTimeStamp = NSDate(timeIntervalSince1970:Double(createdAt)/1000)  //UTC time
                            let dateFormatter = DateFormatter()
                            dateFormatter.timeZone = NSTimeZone.local //Edit
                            dateFormatter.dateFormat = "dd MMM yyyy" //HH:mm
                            let strDateSelect = dateFormatter.string(from: dateTimeStamp as Date)
                            print(strDateSelect)
                            
                            var temp: contact_infor = contact_infor(udid: uid,
                                                                    fullphone: phone,  fullname: fullname,
                                                                    avatarUrl: avatar, created:  strDateSelect)
                            g_contact_Array.append(temp)
                            self.words.append(temp)
                        }
                    }
                }
                
                print(self.words)
                self.generateWordsDict()
                
                ProgressHUD.dismiss()
                completion()
            }
            else {
                Ref_users.removeObserver(withHandle: handle)
                
                ProgressHUD.dismiss()
                completion()
            }
        })
    }
    
    
    //=====================================================================================
    func initAdMobBanner() {
        
        if UIDevice.current.userInterfaceIdiom == .phone {
            // iPhone
            GoogleBannerView.adSize =  GADAdSizeFromCGSize(CGSize(width: 320, height: 50))
            GoogleBannerView.frame = CGRect(x: 0, y: view.frame.size.height, width: 320, height: 50)
        } else  {
            // iPad
            GoogleBannerView.adSize =  GADAdSizeFromCGSize(CGSize(width: 468, height: 60))
            GoogleBannerView.frame = CGRect(x: 0, y: view.frame.size.height, width: 468, height: 60)
        }

        GoogleBannerView.adUnitID = "ca-app-pub-8501671653071605/1974659335"    //"ca-app-pub-8501671653071605/1974659335"    //"ca-app-pub-3535011541485588/6026884358" //ADMOB_BANNER_UNIT_ID
        GoogleBannerView.rootViewController = self
        GoogleBannerView.delegate = self
        
        let request = GADRequest()
        GoogleBannerView.load(request)
        
    }
    
    func adViewDidReceiveAd(_ bannerView: GADBannerView!) {
        print("Banner loaded successfully")
    }
    
    func adView(_ bannerView: GADBannerView!, didFailToReceiveAdWithError error: GADRequestError!) {
        print("Fail to receive ads")
        print(error)
    }
    
    //=====================================================================================
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    //=====================================================================================
    //
    //  Table Delegates
    //
    //=====================================================================================
    
    func generateWordsDict() {
        
        for word in words {
            
            let key = "\(word.fullname[word.fullname.startIndex])"
            //let lower = key.lowercased()
            let lower = key.uppercased()
            
            if var wordValues = wordsDict[lower] {
                wordValues.append(word)
                
                wordsDict[lower] = wordValues
            } else {
                wordsDict[lower] = [word]
            }
        }
        
        wordsSection = [String](wordsDict.keys)
        //wordsSection = wordsSection.sorted()
        
        tableView.reloadData()
        
    }

    func numberOfSections(in tableView: UITableView) -> Int {
        return wordsSection.count
    }
    func tableView(_ tableView: UITableView,
                   titleForHeaderInSection section: Int) -> String?{
        
        return wordsSection[section]
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        let wordkey = wordsSection[section]
        if let wordsValue = wordsDict[wordkey] {
            return wordsValue.count
        }
        return 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "ContactMainCell", for: indexPath) as? ContactMainCell
        
        
        
        if g_contact_Array.count > 0 {
         
            let wordkey = wordsSection[indexPath.section]
            if let wordValues = wordsDict[wordkey.uppercased()] {
                
                //cell?.fullname_Label.text = wordValues[indexPath.row].fullname
                
 
                
                 cell?.avatar_Image.layer.cornerRadius = 25
                 cell?.avatar_Image.layer.borderColor = UIColor.lightGray.cgColor
                 cell?.avatar_Image.layer.borderWidth = 0.2
                 
//                 cell?.avatar_Image.sd_setShowActivityIndicatorView(true)
//                 cell?.avatar_Image.sd_setIndicatorStyle(.gray)
//                 cell?.avatar_Image.sd_setImage(with: URL(string: wordValues[indexPath.row].avatarUrl))
                
                 cell?.fullname_Label.text = wordValues[indexPath.row].fullname
                 cell?.time_Label.text = "Last seen " + wordValues[indexPath.row].created
            }
        }
        return cell!
    }
    
    
    
   
    
    func sectionIndexTitlesForTableView(_ tableView: UITableView) -> [AnyObject]!{
        return wordsIndexTitles as [AnyObject]!
    }
    
    func tableView(_ tableView: UITableView, sectionForSectionIndexTitle title: String, at index: Int) -> Int {
        guard let index = wordsSection.index(of: title) else {
            return -1
        }
        return index
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        let cell = tableView.cellForRow(at: IndexPath(row: indexPath.row, section: 0)) as? ContactMainCell
        
        print(indexPath.section)
        print(indexPath.row)
        print(indexPath.count)
        
        let wordkey = wordsSection[indexPath.section]
        if let wordValues = wordsDict[wordkey.uppercased()] {
            
            print(wordValues[indexPath.row].avatarUrl)
            print(wordValues[indexPath.row].fullname)
            print(wordValues[indexPath.row].created)
        }
        
    }
    
}
