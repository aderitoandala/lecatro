package com.dery.lecatro.util;

import com.dery.lecatro.entity.PlateSequence;
import com.dery.lecatro.entity.enums.Province;
import com.dery.lecatro.repository.PlateSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LicensePlateGenerator {

    private final PlateSequenceRepository plateSequenceRepository;

    
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // total de combinações possíveis por província: 26³ × 1000 = 17.576.000
    private static final long MAX_INDEX = 26L * 26 * 26 * 1000 - 1;

    @Transactional
    public String generate(Province province) {
        String code = province.getCode();

        // busca ou cria a sequência para esta província com bloqueio pessimista
        PlateSequence sequence = plateSequenceRepository
                .findByProvinceForUpdate(code)
                .orElseGet(() -> {
                    PlateSequence newSeq = new PlateSequence(code, -1L);
                    return plateSequenceRepository.save(newSeq);
                });

        // verifica se a sequência esgotou
        if (sequence.getLastIndex() >= MAX_INDEX) {
            throw new IllegalStateException(
                "Sequência de matrículas esgotada para a província: " + code
            );
        }

        // incrementa o índice
        long nextIndex = sequence.getLastIndex() + 1;
        sequence.setLastIndex(nextIndex);
        plateSequenceRepository.save(sequence);

        // converte o índice para o formato ABC 123
        return buildPlateNumber(nextIndex, code);
    }

    private String buildPlateNumber(long index, String provinceCode) {
        // separa a parte das letras (0..17575) da parte dos dígitos (0..999)
        long digitPart  = index % 1000;          // ex: 7 → "007"
        long letterPart = index / 1000;          // ex: 0 → "AAA"

        // converte letterPart para três letras
        int l3 = (int) (letterPart % 26);        // letra mais à direita
        int l2 = (int) ((letterPart / 26) % 26); // letra do meio
        int l1 = (int) (letterPart / 676);       // letra mais à esquerda

        String letters = "" +
            LETTERS.charAt(l1) +
            LETTERS.charAt(l2) +
            LETTERS.charAt(l3);

        // formata os dígitos com zeros à esquerda
        String digits = String.format("%03d", digitPart);

        // formato final: AAA 000 MC
        return letters + " " + digits + " " + provinceCode;
    }
}