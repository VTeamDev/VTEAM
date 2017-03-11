//
//  HHSearchViewController.swift
//  VManga
//
//  Created by mac on 3/10/17.
//  Copyright © 2017 mac. All rights reserved.
//

import UIKit

class HHSearchViewController: UIViewController , UITableViewDelegate , UITableViewDataSource, UICollectionViewDelegate , UICollectionViewDataSource , UISearchBarDelegate {

 
    @IBOutlet var Sview: HHSearchViews!
    
    var searchActive : Bool = false
    var recentSearchBooks : [String] = []
    var books = [Book]()
    var filteredData: [Book]!
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupTableView()
        setupSearchBar()
        setupCollectionView()
        
    }
    
    func setupTableView() {
        Sview.tableview.delegate = self
        Sview.tableview.dataSource = self
        
        let castNib = UINib(nibName: "HHRecentSearchBookTableViewCell", bundle: nil)
        self.Sview.tableview.register(castNib, forCellReuseIdentifier: "HHRecentSearchBookTableViewCell")
        
    }
    
    func setupSearchBar() {
        Sview.searchText.delegate = self
        
        recentSearchBooks.append("abc")
        recentSearchBooks.append("xyz")
        UserDefaults.standard.setValue(self.recentSearchBooks, forKey: "favoriteMovies")
        
        if (UserDefaults.standard.object(forKey: "recentSearchBooks") != nil) {
            self.recentSearchBooks = UserDefaults.standard.object(forKey: "recentSearchBooks") as! [String]
        }
        
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(HHSearchViewController.dismissKeyboard))
        view.addGestureRecognizer(tap)
    }
    
    func setupCollectionView(){
        Sview.collectionView.delegate = self
        Sview.collectionView.dataSource = self
        
        let collectionViewNib = UINib(nibName: "HHBookCollectionViewCell", bundle: nil)
        self.Sview.collectionView.register(collectionViewNib, forCellWithReuseIdentifier: "HHBookCollectionViewCell")
 
        Sview.collectionView.isHidden = true
        self.Sview.footerView.isHidden = true
        
    }
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    func searchBarTextDidEndEditing(_ searchBar: UISearchBar) {
        searchActive = false
        
        self.Sview.collectionView.reloadData()
    }

    func searchBarTextDidBeginEditing(_ searchBar: UISearchBar) {
        searchActive = true;
        
        self.Sview.searchText.showsCancelButton = true
        self.Sview.tableview.isHidden = true
        self.Sview.collectionView.isHidden = false
        self.Sview.footerView.isHidden = false
        
        self.Sview.collectionView.reloadData()
    }

    func searchBarCancelButtonClicked(_ searchBar: UISearchBar) {
        searchActive = false;
        self.Sview.tableview.isHidden = false
        self.Sview.collectionView.isHidden = true
        self.Sview.footerView.isHidden = true
        
        self.Sview.searchText.showsCancelButton = false
        self.Sview.searchText.text = ""
        self.Sview.searchText.resignFirstResponder()
        
        self.Sview.collectionView.reloadData()
    }
    
    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        searchActive = false;
        self.Sview.collectionView.reloadData()
        
    }

    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        SearchManager.search(name: searchText) { (results) in
            for book in results {
                self.books.append(book)
                //print("aaa")
            }
        }
        filteredData = searchText.isEmpty ? books : books.filter { (item: Book) -> Bool in
            // If dataItem matches the searchText, return true to include it
            if (books.count > 0){
                return true
            } else {
                return false
            }
        }
        self.Sview.collectionView.reloadData()
    }
    
    
    // table view
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return recentSearchBooks.count
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = self.Sview.tableview.dequeueReusableCell(withIdentifier: "HHRecentSearchBookTableViewCell") as! HHRecentSearchBookTableViewCell
        cell.bookName.text = recentSearchBooks[indexPath.row]
        return cell
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
    }
    

    /// collection view 
    func collectionView(collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAtIndexPath indexPath: NSIndexPath) -> CGSize {
        
        let dim = collectionView.frame.width/3
        return CGSize(width: dim, height: dim)
    }
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return books.count
    }
        
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = self.Sview.collectionView.dequeueReusableCell(withReuseIdentifier: "HHBookCollectionViewCell", for: indexPath) as! HHBookCollectionViewCell
        cell.label.text = books[indexPath.row].title
        return cell
    }
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        print("selected collection view cell")
    }
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
