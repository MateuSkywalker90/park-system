package com.moliveira.demo_park_api.web.dto.mapper;

import com.moliveira.demo_park_api.entity.Client;
import com.moliveira.demo_park_api.web.dto.ClientCreateDto;
import com.moliveira.demo_park_api.web.dto.ClientResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientMapper {

    public static Client toClient(ClientCreateDto dto) {
        Client client = new Client();
        client.setName(dto.getName());
        client.setCpf(dto.getCpf());
        return client;
    }

    public static ClientResponseDto toDto(Client client) {
        System.out.println("Mapping Client to DTO: " + client);
        return new ModelMapper().map(client, ClientResponseDto.class);
    }
}
