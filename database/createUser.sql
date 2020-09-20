create user if not exists 'lake_data_app' identified by 'Pa$$word8' password expire never;
grant all on pipe_lake.* to 'lake_data_app';