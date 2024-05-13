# Playmaker
This project is a search engine, including web crawling, indexing, ranking, and query processing.

## Built With
[![Java][JAVA]][JAVA-url] [![SpringBoot][Spring-boot]][Spring-boot-url] [![React][React]][React-url]

## Demo Video
https://github.com/Roshdy23/Playmaker/assets/109288170/ff85a55e-3ae5-4028-9641-d6b959d8fcee

## Search Engine Modules

### Web Crawler 
- The web crawler is responsible for collecting documents from the web.
- It starts with a list of URL addresses (seed set) and downloads the documents identified by these URLs.
- Extracts hyperlinks from downloaded documents and adds them to the list of URLs to be downloaded.
- Key features:
    - Avoids revisiting the same page.
    - Crawls documents of specific types (HTML).
    - Maintains state for resuming interrupted crawls.
    - Handles robot.txt exclusions.
    - Provides multithreaded implementation.
    - Crawls a specified number of pages.
    - Uses appropriate data structures for page visit order.

### Indexer 
- Indexes the contents of downloaded HTML documents.
- Features:
    - Persistence in secondary storage.
    - Fast retrieval for word-based queries.
    - Incremental update with newly crawled documents.
    - Considers storage for result ranking and searching.

### Query Processor 
- Processes search queries.
- Performs necessary preprocessing and searches the index for relevant documents.
- Retrieves documents containing words with shared stems from the search query.

### Phrase Searching 
- Supports phrase searching with quotation marks.
- Results must match the order of words in the phrase.

### Ranker 
- Ranks documents based on relevance and popularity.
- Calculates relevance based on query-word appearance and aggregation.
- Measures popularity using algorithms like PageRank.

### Web Interface 
- Implements a user-friendly web interface.
- Receives user queries and displays search results with snippets.
- Displays website title, URL, and relevant paragraph with query words in bold.


## How to Run
1. Clone the repository.
2. Install required dependencies.
3. Run the main application file.
4. Access the React web interface.



[JAVA]: https://img.shields.io/badge/Java-orange?style=for-the-badge&logo=CoffeeScript
[JAVA-url]: https://www.java.com/
[Spring-boot]: https://img.shields.io/badge/springboot-black?style=for-the-badge&logo=springboot
[Spring-boot-url]: https://spring.io/projects/spring-boot
[React]: https://img.shields.io/badge/react-grey?style=for-the-badge&logo=react
[React-url]: https://react.dev/
