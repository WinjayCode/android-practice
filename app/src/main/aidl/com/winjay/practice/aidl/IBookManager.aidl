// IBookManager.aidl
package com.winjay.practice.aidl;

// Declare any non-default types here with import statements
import com.winjay.practice.aidl.Book;

interface IBookManager {
    List<Book> getBookList();
    int getSize();
    void addBook(in Book book);
}
