//
//  HHFavorView.swift
//  VManga
//
//  Created by mac on 3/10/17.
//  Copyright © 2017 mac. All rights reserved.
//

import UIKit

class HHFavorView: UIView , UICollectionViewDelegate , UICollectionViewDataSource {

    /*
    // Only override draw() if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func draw(_ rect: CGRect) {
        // Drawing code
    }
    */
    @IBOutlet var collectionview: UICollectionView!
    @IBOutlet var EmptyFavorBookView: UIView!
    @IBOutlet var favorBookView: UIView!
    
    var favorIdBook = [Int]()
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        
        favorIdBook.append(13152)
        favorIdBook.append(9339)
        favorIdBook.append(13260)
        favorIdBook.append(13466)
        UserDefaults.standard.setValue(favorIdBook, forKey: "favorIdBook")
        print(favorIdBook.count)
//        self.EmptyFavorBookView?.backgroundColor = UIColor(red: 255/255.0, green: 255/255.0, blue: 255/255.0, alpha: 1.0)
        //self.favorBookView.backgroundColor = viewBackgroundColor
        UINib.init(nibName: "HHFavorView", bundle: nil).instantiate(withOwner: self, options: nil)
        if (UserDefaults.standard.object(forKey: "favorIdBook") != nil) {
            self.favorIdBook = UserDefaults.standard.object(forKey: "favorIdBook") as! [Int]
            self.addSubview(favorBookView)
            favorBookView.frame = self.bounds
            setUp()
        } else {
        
        self.addSubview(EmptyFavorBookView)
        EmptyFavorBookView.frame = self.bounds
        }
    }
    func setUp(){
        self.collectionview.delegate = self
        self.collectionview.dataSource = self
        let nib = UINib(nibName: "HHBookCollectionViewCell", bundle: nil)
        self.collectionview.register(nib, forCellWithReuseIdentifier: "HHBookCollectionViewCell")
    }
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return (favorIdBook.count)
    }
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "HHBookCollectionViewCell", for: indexPath) as! HHBookCollectionViewCell
        API.getMangaInfo(manga_id: (favorIdBook[indexPath.row])).then { (book) -> Void in
             cell.label.text = book.title
            let defaultImage = UIImage(named: "Vmanga-icon")
            let url = URL(string: book.thumbnail)
            cell.image.sd_setImage(with: url, placeholderImage: defaultImage)
        
            }.catch { e in

        }
        return cell
    }
}
