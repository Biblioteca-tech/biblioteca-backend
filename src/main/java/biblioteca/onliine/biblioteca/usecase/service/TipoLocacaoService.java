package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.TipoAcesso;
import biblioteca.onliine.biblioteca.domain.entity.TipoLocacao;
import biblioteca.onliine.biblioteca.domain.port.repository.TipoAcessoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TipoLocacaoService {
    private final TipoAcessoRepository tipoAcessoRepository;

    public TipoLocacaoService(TipoAcessoRepository tipoAcessoRepository) {
        this.tipoAcessoRepository = tipoAcessoRepository;
    }

    public boolean usuarioPodeBaixar(Long livroId, Long clienteId) {
        Optional<TipoLocacao> registro =
                tipoAcessoRepository.findByClienteIdAndLivroId(clienteId, livroId);

        if (registro.isEmpty()) return false;

        return registro.get().getTipoAcesso().equals(TipoAcesso.COMPRADO);
    }

}
