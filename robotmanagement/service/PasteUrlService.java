package com.example.robotmanagement.service;

import com.example.robotmanagement.entity.PasteUrl;
import com.example.robotmanagement.repository.PasteUrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PasteUrlService {

    private final PasteUrlRepository pasteUrlRepository;

    @Autowired
    public PasteUrlService(PasteUrlRepository pasteUrlRepository) {
        this.pasteUrlRepository = pasteUrlRepository;
    }

    public void updateMainUrl(String newUrl) {
        Optional<PasteUrl> optionalPasteUrl = pasteUrlRepository.findById(1L);

        PasteUrl pasteUrl;
        if (optionalPasteUrl.isPresent()) {
            pasteUrl = optionalPasteUrl.get();
            pasteUrl.setUrl(newUrl);
        } else {
            pasteUrl = new PasteUrl();
            pasteUrl.setId(1L); // only set ID when truly creating
            pasteUrl.setUrl(newUrl);
        }

        pasteUrlRepository.save(pasteUrl);
    }


    public String getMainUrl() {
        return pasteUrlRepository.findById(1L)
                .map(PasteUrl::getUrl)
                .orElse(null);
    }
}
