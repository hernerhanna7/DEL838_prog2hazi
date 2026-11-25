package library;

import java.util.ArrayList;
import java.util.List;

public class TitleSearchStrategy implements SearchStrategy {
    @Override
    public List<LibraryItem> search(List<LibraryItem> items, String query) {
        List<LibraryItem> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (LibraryItem item : items) {
            if (item.getTitle().toLowerCase().contains(lowerQuery)) {
                results.add(item);
            }
        }
        return results;
    }
}
