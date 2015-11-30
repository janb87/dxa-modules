package com.sdl.dxa.modules.search.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sdl.dxa.modules.search.serializer.FlatRichTextSerializer;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.sdl.webapp.common.util.StringUtils.convertFormatStringFromCM;
import static com.tridion.util.URIUtils.urlDecode;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * Base class for Search Query/Results.
 */
@SemanticEntity(entityName = "ItemList", prefix = "s", vocabulary = SemanticVocabulary.SDL_CORE, public_ = true)
public class SearchQuery<T extends SearchItem> extends AbstractEntityModel {

    @SemanticProperty("s:headline")
    private String headline;

    @JsonProperty("ResultsText")
    @JsonSerialize(using = FlatRichTextSerializer.class)
    private RichText resultsText;

    @JsonProperty("NoResultsText")
    @JsonSerialize(using = FlatRichTextSerializer.class)
    private RichText noResultsText;

    private QueryDetails queryDetails;

    private PagerDetails pagerDetails;

    @SemanticProperty("s:itemListElement")
    private List<T> results = new ArrayList<>();

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public RichText getResultsText() {
        return resultsText;
    }

    public void setResultsText(RichText resultsText) {
        this.resultsText = resultsText;
    }

    public RichText getNoResultsText() {
        return noResultsText;
    }

    public void setNoResultsText(RichText noResultsText) {
        this.noResultsText = noResultsText;
    }

    public String formatResultsText() {
        return String.format(convertFormatStringFromCM(resultsText.toString()),
                queryDetails.getQueryText(), pagerDetails.getTotal());
    }

    public String formatNoResultsText() {
        return String.format(convertFormatStringFromCM(noResultsText.toString()), queryDetails.getQueryText());
    }

    public QueryDetails getQueryDetails() {
        return queryDetails;
    }

    public void setQueryDetails(QueryDetails queryDetails) {
        this.queryDetails = queryDetails;
    }

    public PagerDetails getPagerDetails() {
        return pagerDetails;
    }

    public void setPagerDetails(PagerDetails pagerDetails) {
        this.pagerDetails = pagerDetails;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public String pagerLink(int position) {
        return String.format("?start=%d&q=%s", position, queryDetails.getQueryText());
    }

//    @Override
//    public MvcData getMvcData() {
//        return new MvcDataImpl("Search:SearchResults");
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SearchQuery that = (SearchQuery) o;
        return Objects.equals(headline, that.headline) &&
                Objects.equals(resultsText, that.resultsText) &&
                Objects.equals(noResultsText, that.noResultsText) &&
                Objects.equals(queryDetails, that.queryDetails) &&
                Objects.equals(pagerDetails, that.pagerDetails) &&
                Objects.equals(results, that.results);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), headline, resultsText, noResultsText, queryDetails, pagerDetails, results);
    }

    /**
     * Query details mapped by the Search Controller to query string parameters.
     */
    public static class QueryDetails {
        private String queryText;
        private Map<String, String[]> queryStringParameters;

        public QueryDetails(String queryText, Map<String, String[]> queryStringParameters) {
            this.queryText = queryText;
            this.queryStringParameters = queryStringParameters;
        }

        public String getQueryText() {
            return isEmpty(queryText) ? "" : urlDecode(queryText);
        }

        public void setQueryText(String queryText) {
            this.queryText = queryText;
        }

        public Map<String, String[]> getQueryStringParameters() {
            return queryStringParameters;
        }

        public void setQueryStringParameters(Map<String, String[]> queryStringParameters) {
            this.queryStringParameters = queryStringParameters;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            QueryDetails that = (QueryDetails) o;
            return Objects.equals(queryText, that.queryText) &&
                    Objects.equals(queryStringParameters, that.queryStringParameters);
        }

        @Override
        public int hashCode() {
            return Objects.hash(queryText, queryStringParameters);
        }
    }

    /**
     * Reflects the current position of pager (set by the Search Provider).
     */
    public static class PagerDetails {
        private int start;
        private long total;
        private int pageSize = 7;

        public PagerDetails(int start) {
            this.start = start;
        }

        public PagerDetails(int start, long total) {
            this.start = start;
            this.total = total;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getCurrentPage() {
            return 1 + (getStart() - 1) / getPageSize();
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public boolean hasMoreResults() {
            return (getStart() + getPageSize()) <= getTotal();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PagerDetails that = (PagerDetails) o;
            return start == that.start &&
                    total == that.total;
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, total);
        }
    }
}
