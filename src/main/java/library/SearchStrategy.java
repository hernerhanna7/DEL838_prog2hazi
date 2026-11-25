package library;

import java.util.List;

public interface SearchStrategy {
    List<LibraryItem> search(List<LibraryItem> items, String query);
}
