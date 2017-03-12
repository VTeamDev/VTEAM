//
//  HHBookDetailViewController.swift
//  VManga
//
//  Created by mac on 3/12/17.
//  Copyright © 2017 mac. All rights reserved.
//

import UIKit
import SCLAlertView

class HHBookDetailViewController: UIViewController {
    
    var isFavor : Bool!
    var indexOfMoviesfavor : Int = 0
    var bookId : [Int] = []
    var book = Book()
    @IBAction func invokeBtnFavor(_ sender: Any) {
        isFavor = false
        indexOfMoviesfavor = 0
        if (UserDefaults.standard.array(forKey: "favoriteBook") != nil) {
            bookId = UserDefaults.standard.array(forKey: "favoriteBook") as! [Int]
            for id in bookId {
                if (id == book.manga_id){
                    isFavor = true
                    
                   let mess = ""
                    let actionSheetControllerIOS8: UIAlertController = UIAlertController(title: "", message: mess, preferredStyle: .actionSheet)
                    
                    let cancelActionButton: UIAlertAction = UIAlertAction(title: "Cancel", style: .cancel) { action -> Void in
                        print("Cancel")
                    }
                    actionSheetControllerIOS8.addAction(cancelActionButton)
                    
                    let deleteActionButton: UIAlertAction = UIAlertAction(title: "Remove from Favorite", style: .destructive)
                    { action -> Void in
                        self.bookId.remove(at: self.indexOfMoviesfavor)
                        
                        UserDefaults.standard.setValue(self.bookId, forKey: "favoriteBook")
                        self.favorBtn.setImage(UIImage(named: "unlike.png"), for: .normal)
                    }
                    actionSheetControllerIOS8.addAction(deleteActionButton)
                    self.present(actionSheetControllerIOS8, animated: true, completion: nil)
                    
                    return
                }
                indexOfMoviesfavor += 1
            }
        }
        if (!isFavor){
            bookId.append(book.manga_id)
            showFavor()
            favorBtn.setImage(UIImage(named: "like.png"), for: .normal)
            UserDefaults.standard.setValue(bookId, forKey: "favoriteMovies")
        }

    }
    
    func showFavor(){
        let alertview = SCLAlertView()
        alertview.showSuccess("Success", subTitle: "Movie added to Favorite")
    }
    
    @IBOutlet var favorBtn: UIButton!
    @IBOutlet var tableview: UITableView!
    @IBOutlet var content: UILabel!
    
    @IBOutlet var bookName: UILabel!
    @IBOutlet var image: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setUpFavorButton()
        setUpBookInfo()
        
        navigationController?.setNavigationBarHidden(false, animated: false)
        navigationController?.navigationBar.barTintColor = colors.navigator

        
        self.tableview.delegate = self
        self.tableview.dataSource = self
        
        let nib = UINib(nibName: "HHChapterBookDetailTableViewCell", bundle: nil)
        self.tableview.register(nib, forCellReuseIdentifier: "HHChapterBookDetailTableViewCell")
    }

    func setUpBookInfo(){
        
        let defaultImage = UIImage(named: "Vmanga-icon")
        let urlImage = URL(string: book.thumbnail)
        self.image.sd_setImage(with: urlImage, placeholderImage: defaultImage)
        self.bookName.text = book.title
        self.content.text = book.description
        
    }
    

    func setUpFavorButton()  {
        isFavor = false
        if (UserDefaults.standard.array(forKey: "favoriteMovies") != nil) {
            bookId = UserDefaults.standard.array(forKey: "favoriteMovies") as! [Int]
            for id in bookId {
                if (id == book.manga_id){
                    isFavor = true
                    favorBtn.setImage(UIImage(named: "like.png"), for: .normal)
                    return
                }
                indexOfMoviesfavor += 1
            }
        }
        if (!isFavor){
            favorBtn.setImage(UIImage(named: "unlike.png"), for: .normal)
        }
        
    }
}

extension HHBookDetailViewController: UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return book.numberOfChapters
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = self.tableview.dequeueReusableCell(withIdentifier: "HHChapterBookDetailTableViewCell", for: indexPath) as! HHChapterBookDetailTableViewCell
        cell.label.text = "Chapter " + String(indexPath.row)
        
        return cell
    }
}

extension HHBookDetailViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let index = indexPath.row
        let readingViewController = storyboard?.instantiateViewController(withIdentifier: "HHReadingViewController") as! HHReadingViewController
        readingViewController.loadChapter(manga_id: book.manga_id, chapterId: index)
        API.postReadingManga(_id_manga: book._id)
        present(readingViewController, animated: true, completion: nil)
    }
}
