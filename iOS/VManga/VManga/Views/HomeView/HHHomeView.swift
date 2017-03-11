//
//  HHHomeView.swift
//  VManga
//
//  Created by mac on 3/11/17.
//  Copyright © 2017 mac. All rights reserved.
//

import UIKit

class HHHomeView: UITableView ,UITableViewDataSource  , UITableViewDelegate {

    var topBooks = [Book]()
    var lastestBooks = [Book]()
    
    override func awakeFromNib() {
        super.awakeFromNib()
        self.dataSource = self
        self.delegate = self
    }
    
    func setUp(){

        let nib = UINib(nibName: "HHHomeTableViewCell", bundle: nil)
        self.register(nib, forCellReuseIdentifier: "HHHomeTableViewCell")
        self.rowHeight = self.frame.height * 1.3/2
    }
    
    override func numberOfRows(inSection section: Int) -> Int {
        return 1
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 2
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = dequeueReusableCell(withIdentifier: "HHHomeTableViewCell", for: indexPath) as! HHHomeTableViewCell
        cell.setUp()
        switch indexPath.row {
        case 0:
            cell.label.text = "Truyện phổ biến trong tuần"
            cell.book = topBooks
        default:
            cell.label.text = "Truyện mới cập nhật"
            cell.book = lastestBooks
        }
        
        return cell
    }

   // @IBOutlet weak var tableview: UITableView!
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
