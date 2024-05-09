package dev.engine.searchengine;

import org.bson.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/queryquest/")
public class LinkController {
    private final LinkRepository linkRepository;
    public LinkController(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }
    @CrossOrigin
    @GetMapping("{query}")
    List<Link> search(@PathVariable String query) {
        linkRepository.addQueryForSuggestions(query);
        return linkRepository.search(query);
    }
    @CrossOrigin
    @GetMapping("/prvQueries/{query}")
    List<String> prvQueries(@PathVariable String query) {
        return linkRepository.prevMatchedQueries(query);
    }

}
