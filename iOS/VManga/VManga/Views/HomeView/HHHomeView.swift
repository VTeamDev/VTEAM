//
//  HHHomeView.swift
//  VManga
//
//  Created by mac on 3/11/17.
//  Copyright © 2017 mac. All rights reserved.
//

import UIKit
import ImageSlideshow
import AFNetworking


class HHHomeView: UIView ,UITableViewDataSource  , UITableViewDelegate {

    @IBOutlet var sildeshow: ImageSlideshow!
    @IBOutlet weak var tableview: UITableView!
    var topBooks = [Book]()
    var lastestBooks = [Book]()
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
        
    }
    
    
    
    let afNetworkingSource = [AFURLSource(urlString: "https://images.unsplash.com/photo-1432679963831-2dab49187847?w=1080")!, AFURLSource(urlString: "https://images.unsplash.com/photo-1447746249824-4be4e1b76d66?w=1080")!, AFURLSource(urlString: "https://images.unsplash.com/photo-1463595373836-6e0b0a8ee322?w=1080")!]
    
    func setUp(){
        
        setUpSlideShow()
        
        self.tableview.dataSource = self
        self.tableview.delegate = self
        
        let nib = UINib(nibName: "HHHomeTableViewCell", bundle: nil)
        self.tableview.register(nib, forCellReuseIdentifier: "HHHomeTableViewCell")
        self.tableview.rowHeight = self.frame.height * 1.4/4
        
    }
    
    func deselectCell()  {
        if let index = self.tableview.indexPathForSelectedRow{
            self.tableview.deselectRow(at: index, animated: true)
        }
    }
    
    func setUpSlideShow(){
        sildeshow.circular = true
        
        sildeshow.backgroundColor = UIColor.black
        sildeshow.slideshowInterval = 5.0
        sildeshow.pageControlPosition = PageControlPosition.insideScrollView
     //   sildeshow.pageControl.currentPageIndicatorTintColor = UIColor.lightGray
      //  sildeshow.pageControl.pageIndicatorTintColor = UIColor.black
        let image = UIImageView(image: UIImage(named: "1"))
        image.frame = sildeshow.frame
        sildeshow.setImageInputs([
            ImageSource(image: image.image!),
            ImageSource(image: image.image!)])
        sildeshow.contentScaleMode = UIViewContentMode.scaleAspectFit
        
        sildeshow.currentPageChanged = { page in
            //print("current page:", page)
        }

        //sildeshow.setImageInputs(afNetworkingSource)
        
        
        
        let recognizer = UITapGestureRecognizer(target: self, action: #selector(self.didTap))
        sildeshow.addGestureRecognizer(recognizer)
    }
    
    func didTap() {
     //   sildeshow.presentFullScreenController(from: sel)
    }
    
     func numberOfRows(inSection section: Int) -> Int {
        return 1
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 2
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = self.tableview.dequeueReusableCell(withIdentifier: "HHHomeTableViewCell", for: indexPath) as! HHHomeTableViewCell
        cell.setUp()
        switch indexPath.row {
        case 0:
            cell.label.text = "Truyện phổ biến trong tuần"
            cell.books = topBooks
        default:
            cell.label.text = "Truyện mới cập nhật"
            cell.books = lastestBooks
        }
        
        return cell
    }
    
//    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
//        self.tableView.deselectRow(at: indexPath, animated: true)
//    }
//    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
//        return self.frame.height/4
//    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        deselectCell()
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
