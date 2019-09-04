// IBookManager.aidl
package com.winjay.practice.aidl;

// Declare any non-default types here with import statements
import com.winjay.practice.aidl.Book;

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
}
