project(bird)

find_library(FREEGLUT_LIBRARY freeglut)
set(extra_dependencies "")
set(extra_libraries "")
if (FREEGLUT_LIBRARY STREQUAL "FREEGLUT_LIBRARY-NOTFOUND")
	add_subdirectory("${OSMAND_ROOT}/tools/cpp-tools/map-viewer/externals/freeglut" "externals/freeglut")
	set(extra_dependencies ${extra_dependencies}
		freeglut_static
	)
	set(extra_libraries ${extra_libraries}
		freeglut_static
	)
else()
	set(extra_libraries ${extra_libraries}
		${FREEGLUT_LIBRARY}
	)
endif()

if (CMAKE_SHARED_LIBS_ALLOWED_ON_TARGET)
	add_executable(bird
		"main.cpp"
	)

	add_dependencies(bird
		OsmAndCore_shared
		OsmAndCore_ResourcesBundle_shared
		${extra_dependencies}
	)
	target_link_libraries(bird
		OsmAndCore_shared
		OsmAndCore_ResourcesBundle_shared
		${extra_libraries}
	)
endif()

if (TARGET OsmAndCore_static_standalone)
	add_executable(bird_standalone
		"main.cpp"
	)

	add_dependencies(bird_standalone
		OsmAndCore_static_standalone
		OsmAndCore_ResourcesBundle_static
		${extra_dependencies}
	)
	target_link_libraries(bird_standalone
		OsmAndCore_static_standalone
		${extra_libraries}
	)
	link_entire_static_library(bird_standalone OsmAndCore_ResourcesBundle_static)
endif()
