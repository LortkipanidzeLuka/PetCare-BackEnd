package ge.edu.freeuni.petcarebackend.repository.generic.search;

public class SearchCriteria<T> {

    private String key;

    private Object value;

    private SearchOperation operation;

    private CustomCriteria<T> customCriteria;

    public SearchCriteria(String key, Object value, SearchOperation operation) {
        this.key = key;
        this.value = value;
        this.operation = operation;
    }

    public SearchCriteria(CustomCriteria<T> customCriteria) {
        this.customCriteria = customCriteria;
        this.operation = SearchOperation.CUSTOM_CRITERIA;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public SearchOperation getOperation() {
        return operation;
    }

    public void setOperation(SearchOperation operation) {
        this.operation = operation;
    }

    public CustomCriteria<T> getCustomCriteria() {
        return customCriteria;
    }

    public void setCustomCriteria(CustomCriteria<T> customCriteria) {
        this.customCriteria = customCriteria;
    }
}