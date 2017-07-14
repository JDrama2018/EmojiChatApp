import UIKit
import SinchVerification;

class CountrySelectionViewController : UITableViewController {
    
    var isoCountryCode: String?
    
    var onCompletion: ((String) -> Void)?
    
    var entries: Array<SINRegionInfo> = [];
    
    func PhoneNumberUtil() -> PhoneNumberUtil {
        return SharedPhoneNumberUtil();
    }
    
    override func viewDidLoad() {
        super.viewDidLoad();
        let regionList = PhoneNumberUtil().regionList(forLocale: Locale.current);
        entries = regionList.entries.sorted(by: { (a: SINRegionInfo, b: SINRegionInfo) -> Bool in
            return a.countryDisplayName < b.countryDisplayName;
        })
    }
    
    override func viewWillAppear(_ animated: Bool) {
        let row = entries.index { (region: SINRegionInfo) -> Bool in
            return region.isoCountryCode == isoCountryCode;
        }
        if row != nil {
            tableView.selectRow(at: IndexPath.init(row: Int(row!), section: 0), animated: animated, scrollPosition: .middle);
        }
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return entries.count;
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "country", for: indexPath);
        
        let entry = entries[indexPath.row];
        
        cell.textLabel?.text  = entry.countryDisplayName;
        cell.detailTextLabel?.text = String(format:"(+%@)", entry.countryCallingCode);
        
        return cell;
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        onCompletion?(entries[indexPath.row].isoCountryCode);
    }
    
    @IBAction func cancelButtonTapped(_ sender: Any) {
        dismiss(animated: true, completion: nil);
    }
}
