package at.timeguess.backend.ui.converters;

import java.util.Optional;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Cube;
import at.timeguess.backend.services.CubeService;

/**
 * A converter for {@Cube} objects.
 * 
 * We need this to get the full cube object into a controller
 * (e.g., a {@link CubeConfigurationController) from a request parameter
 * that is just the cube's id.
 */
@Component
public class CubeConverter implements Converter<Cube> {

    @Autowired
    private CubeService cubeService;

    @Override
    public Cube getAsObject(FacesContext fc, UIComponent uic, String cubeId) {
        Optional<Cube> cube = cubeService.findById(Long.valueOf(cubeId));
        if (cube.isPresent()) {
            return cube.get();
        }
        else {
            throw new ConverterException();
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Cube cube) {
        return cube.getId().toString();
    }

}
