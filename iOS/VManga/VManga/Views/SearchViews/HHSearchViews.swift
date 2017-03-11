//
//  HHSearchViews.swift
//  VManga
//
//  Created by mac on 3/10/17.
//  Copyright © 2017 mac. All rights reserved.
//

import UIKit

class HHSearchViews: UIView,UITableViewDelegate {

    /*
    // Only override draw() if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func draw(_ rect: CGRect) {
        // Drawing code
    }
    */
   // @IBOutlet var view: UIView!
    var resetRecentBook = [String]()
    @IBOutlet var view: HHSearchViews!
    @IBOutlet weak var footerView: UIView!

    @IBOutlet weak var collectionView: UICollectionView!
   
    @IBOutlet weak var tableview: UITableView!
    
    @IBOutlet weak var searchText: UISearchBar!

    @IBAction func invokeDelete(_ sender: Any) {
        
       
        UserDefaults.standard.setValue(self.resetRecentBook, forKey: "recentSearchBooks")
        self.tableview.reloadData()
    }
    required init?(coder aDecoder: NSCoder) {
        
        super.init(coder: aDecoder)
       //collectionView.isHidden = true
       UINib.init(nibName: "HHSearchViews", bundle: nil).instantiate(withOwner: self, options: nil)
        self.addSubview(view)
        view.frame = self.bounds
        self.tableview.delegate = self
    }
}
