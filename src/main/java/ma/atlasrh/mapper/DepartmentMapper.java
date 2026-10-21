
package ma.atlasrh.mapper;

import ma.atlasrh.dto.DepartmentRequest;
import ma.atlasrh.dto.DepartmentResponse;
import ma.atlasrh.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    Department toEntity(DepartmentRequest request);

    DepartmentResponse toResponse(Department department);

    void updateEntity(
            DepartmentRequest request,
            @MappingTarget Department department
    );
}
